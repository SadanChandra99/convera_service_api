package com.convera.product.configuration;

import com.convera.avro.schemas.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class KafkaCustomConsumer implements
        Callable<Set<PaymentEvent>> {

    private Consumer<String,PaymentEvent> consumer = null;
    Map<String,String> kafkaProperties;
    AtomicBoolean stopConsumer =  new AtomicBoolean(false);

    private int retry;

    public String topic;

    public KafkaCustomConsumer(ConsumerFactory<String, PaymentEvent> consumerFactory, String topic) {
        this.consumer = consumerFactory.createConsumer();
        this.topic = topic;
    }


    @Override
    public Set<PaymentEvent> call() throws Exception {
        Set<PaymentEvent> set = new HashSet<>();
        try {
            consumer.subscribe(List.of(topic));
            while(!stopConsumer.get()){
                pollRecords(set);
            }
            if(stopConsumer.get()){
                consumer.wakeup();
                log.info("consumer wakeup executed...");
            }
        } catch (WakeupException e) {
            log.error("exception in call() WakeupException: ", e);
        } catch (Exception e) {
            log.error("exception in call() Exception: ", e);
        } finally {
            try {
                //commented due to we dont want to set the offset and fetch all the messages
                //consumer.commitSync();
            } catch (CommitFailedException e) {
                log.error("commit failed", e);
            }
            log.info("consumer close...");
            consumer.close();
        }

        return set;
    }

    private void pollRecords(Set<PaymentEvent> set) throws Exception {
        retry ++;
        ConsumerRecords<String,PaymentEvent> records = consumer.poll(1000);
        log.debug("------records size: "+records.count());
        if(records.count()==0 && retry>=3){
            this.stopConsumer = new AtomicBoolean(true);
        } else if(records.count()>0) {
            retry = 0;
        }
        for (ConsumerRecord<String, PaymentEvent> record : records)
        {
            set.add(record.value());
            log.info(
                    String.format("\n--------- topic = %s, partition = %d, offset = %d, customer = %s, value = %s",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value())
            );
        }

    }
}
