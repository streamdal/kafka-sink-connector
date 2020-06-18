package main

import (
	"fmt"
	"context"
	"crypto/tls"
	"sync"
	"time"

	"github.com/segmentio/kafka-go"
)

var (
	brokers = []string{"b-2.sandbox.5oabwj.c2.kafka.us-west-2.amazonaws.com:9094","b-3.sandbox.5oabwj.c2.kafka.us-west-2.amazonaws.com:9094","b-1.sandbox.5oabwj.c2.kafka.us-west-2.amazonaws.com:9094"}
	topic = "inbound"
)

func main() {
	// to produce messages
	dialer := &kafka.Dialer{
		Timeout:   10 * time.Second,
		DualStack: true,
		TLS:       &tls.Config{
			InsecureSkipVerify: true,
		},
	}

	var wg sync.WaitGroup

	w := kafka.NewWriter(kafka.WriterConfig{
		Brokers: brokers,
		Topic:   topic,
		Balancer: &kafka.LeastBytes{},
		Dialer: dialer,
	})

	for i := 0; i < 50000; i++ {
		wg.Add(1)
		i := i
		go func(i int, wg *sync.WaitGroup){
			defer wg.Done()
			err := w.WriteMessages(context.Background(),
				kafka.Message{
					Key:   []byte(fmt.Sprintf("Key-%d", i)),
					Value: []byte(fmt.Sprintf("Value-%d", i)),
				})
			if err != nil {
				panic(err)
			}
			fmt.Println(i)
		}(i, &wg)
	}

	//err := w.WriteMessages(context.Background(),
	//	kafka.Message{
	//		Key:   []byte("Key-A"),
	//		Value: []byte("Hello World!"),
	//	},
	//	kafka.Message{
	//		Key:   []byte("Key-B"),
	//		Value: []byte("One!"),
	//	},
	//	kafka.Message{
	//		Key:   []byte("Key-C"),
	//		Value: []byte("Two!"),
	//	},
	//)
	//if err != nil {
	//	panic(err)
	//}

	wg.Wait()
	w.Close()
}
