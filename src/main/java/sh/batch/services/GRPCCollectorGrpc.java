package sh.batch.services;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: grpc-collector.proto")
public final class GRPCCollectorGrpc {

  private GRPCCollectorGrpc() {}

  public static final String SERVICE_NAME = "services.GRPCCollector";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<sh.batch.services.GenericRecordRequest,
      sh.batch.services.GenericRecordResponse> METHOD_ADD_RECORD =
      io.grpc.MethodDescriptor.<sh.batch.services.GenericRecordRequest, sh.batch.services.GenericRecordResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "services.GRPCCollector", "AddRecord"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.GenericRecordRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.GenericRecordResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<sh.batch.services.AMQPRecordRequest,
      sh.batch.services.AMQPRecordResponse> METHOD_ADD_AMQPRECORD =
      io.grpc.MethodDescriptor.<sh.batch.services.AMQPRecordRequest, sh.batch.services.AMQPRecordResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "services.GRPCCollector", "AddAMQPRecord"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.AMQPRecordRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.AMQPRecordResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<sh.batch.services.KafkaSinkRecordRequest,
      sh.batch.services.KafkaSinkRecordResponse> METHOD_ADD_KAFKA_RECORD =
      io.grpc.MethodDescriptor.<sh.batch.services.KafkaSinkRecordRequest, sh.batch.services.KafkaSinkRecordResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "services.GRPCCollector", "AddKafkaRecord"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.KafkaSinkRecordRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.KafkaSinkRecordResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<sh.batch.services.SQSRecordRequest,
      sh.batch.services.SQSRecordResponse> METHOD_ADD_SQSRECORD =
      io.grpc.MethodDescriptor.<sh.batch.services.SQSRecordRequest, sh.batch.services.SQSRecordResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "services.GRPCCollector", "AddSQSRecord"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.SQSRecordRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.SQSRecordResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<sh.batch.services.TestRequest,
      sh.batch.services.TestResponse> METHOD_TEST =
      io.grpc.MethodDescriptor.<sh.batch.services.TestRequest, sh.batch.services.TestResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "services.GRPCCollector", "Test"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.TestRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.TestResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GRPCCollectorStub newStub(io.grpc.Channel channel) {
    return new GRPCCollectorStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GRPCCollectorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GRPCCollectorBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GRPCCollectorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GRPCCollectorFutureStub(channel);
  }

  /**
   */
  public static abstract class GRPCCollectorImplBase implements io.grpc.BindableService {

    /**
     */
    public void addRecord(sh.batch.services.GenericRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.GenericRecordResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ADD_RECORD, responseObserver);
    }

    /**
     */
    public void addAMQPRecord(sh.batch.services.AMQPRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.AMQPRecordResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ADD_AMQPRECORD, responseObserver);
    }

    /**
     */
    public void addKafkaRecord(sh.batch.services.KafkaSinkRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.KafkaSinkRecordResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ADD_KAFKA_RECORD, responseObserver);
    }

    /**
     */
    public void addSQSRecord(sh.batch.services.SQSRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.SQSRecordResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ADD_SQSRECORD, responseObserver);
    }

    /**
     */
    public void test(sh.batch.services.TestRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.TestResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_TEST, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_ADD_RECORD,
            asyncUnaryCall(
              new MethodHandlers<
                sh.batch.services.GenericRecordRequest,
                sh.batch.services.GenericRecordResponse>(
                  this, METHODID_ADD_RECORD)))
          .addMethod(
            METHOD_ADD_AMQPRECORD,
            asyncUnaryCall(
              new MethodHandlers<
                sh.batch.services.AMQPRecordRequest,
                sh.batch.services.AMQPRecordResponse>(
                  this, METHODID_ADD_AMQPRECORD)))
          .addMethod(
            METHOD_ADD_KAFKA_RECORD,
            asyncUnaryCall(
              new MethodHandlers<
                sh.batch.services.KafkaSinkRecordRequest,
                sh.batch.services.KafkaSinkRecordResponse>(
                  this, METHODID_ADD_KAFKA_RECORD)))
          .addMethod(
            METHOD_ADD_SQSRECORD,
            asyncUnaryCall(
              new MethodHandlers<
                sh.batch.services.SQSRecordRequest,
                sh.batch.services.SQSRecordResponse>(
                  this, METHODID_ADD_SQSRECORD)))
          .addMethod(
            METHOD_TEST,
            asyncUnaryCall(
              new MethodHandlers<
                sh.batch.services.TestRequest,
                sh.batch.services.TestResponse>(
                  this, METHODID_TEST)))
          .build();
    }
  }

  /**
   */
  public static final class GRPCCollectorStub extends io.grpc.stub.AbstractStub<GRPCCollectorStub> {
    private GRPCCollectorStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCCollectorStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCCollectorStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCCollectorStub(channel, callOptions);
    }

    /**
     */
    public void addRecord(sh.batch.services.GenericRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.GenericRecordResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ADD_RECORD, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addAMQPRecord(sh.batch.services.AMQPRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.AMQPRecordResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ADD_AMQPRECORD, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addKafkaRecord(sh.batch.services.KafkaSinkRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.KafkaSinkRecordResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ADD_KAFKA_RECORD, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addSQSRecord(sh.batch.services.SQSRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.SQSRecordResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ADD_SQSRECORD, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void test(sh.batch.services.TestRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.TestResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_TEST, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GRPCCollectorBlockingStub extends io.grpc.stub.AbstractStub<GRPCCollectorBlockingStub> {
    private GRPCCollectorBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCCollectorBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCCollectorBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCCollectorBlockingStub(channel, callOptions);
    }

    /**
     */
    public sh.batch.services.GenericRecordResponse addRecord(sh.batch.services.GenericRecordRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ADD_RECORD, getCallOptions(), request);
    }

    /**
     */
    public sh.batch.services.AMQPRecordResponse addAMQPRecord(sh.batch.services.AMQPRecordRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ADD_AMQPRECORD, getCallOptions(), request);
    }

    /**
     */
    public sh.batch.services.KafkaSinkRecordResponse addKafkaRecord(sh.batch.services.KafkaSinkRecordRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ADD_KAFKA_RECORD, getCallOptions(), request);
    }

    /**
     */
    public sh.batch.services.SQSRecordResponse addSQSRecord(sh.batch.services.SQSRecordRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ADD_SQSRECORD, getCallOptions(), request);
    }

    /**
     */
    public sh.batch.services.TestResponse test(sh.batch.services.TestRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_TEST, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GRPCCollectorFutureStub extends io.grpc.stub.AbstractStub<GRPCCollectorFutureStub> {
    private GRPCCollectorFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GRPCCollectorFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GRPCCollectorFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GRPCCollectorFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<sh.batch.services.GenericRecordResponse> addRecord(
        sh.batch.services.GenericRecordRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ADD_RECORD, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<sh.batch.services.AMQPRecordResponse> addAMQPRecord(
        sh.batch.services.AMQPRecordRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ADD_AMQPRECORD, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<sh.batch.services.KafkaSinkRecordResponse> addKafkaRecord(
        sh.batch.services.KafkaSinkRecordRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ADD_KAFKA_RECORD, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<sh.batch.services.SQSRecordResponse> addSQSRecord(
        sh.batch.services.SQSRecordRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ADD_SQSRECORD, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<sh.batch.services.TestResponse> test(
        sh.batch.services.TestRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_TEST, getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD_RECORD = 0;
  private static final int METHODID_ADD_AMQPRECORD = 1;
  private static final int METHODID_ADD_KAFKA_RECORD = 2;
  private static final int METHODID_ADD_SQSRECORD = 3;
  private static final int METHODID_TEST = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GRPCCollectorImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GRPCCollectorImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD_RECORD:
          serviceImpl.addRecord((sh.batch.services.GenericRecordRequest) request,
              (io.grpc.stub.StreamObserver<sh.batch.services.GenericRecordResponse>) responseObserver);
          break;
        case METHODID_ADD_AMQPRECORD:
          serviceImpl.addAMQPRecord((sh.batch.services.AMQPRecordRequest) request,
              (io.grpc.stub.StreamObserver<sh.batch.services.AMQPRecordResponse>) responseObserver);
          break;
        case METHODID_ADD_KAFKA_RECORD:
          serviceImpl.addKafkaRecord((sh.batch.services.KafkaSinkRecordRequest) request,
              (io.grpc.stub.StreamObserver<sh.batch.services.KafkaSinkRecordResponse>) responseObserver);
          break;
        case METHODID_ADD_SQSRECORD:
          serviceImpl.addSQSRecord((sh.batch.services.SQSRecordRequest) request,
              (io.grpc.stub.StreamObserver<sh.batch.services.SQSRecordResponse>) responseObserver);
          break;
        case METHODID_TEST:
          serviceImpl.test((sh.batch.services.TestRequest) request,
              (io.grpc.stub.StreamObserver<sh.batch.services.TestResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class GRPCCollectorDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return sh.batch.services.GrpcCollector.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GRPCCollectorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GRPCCollectorDescriptorSupplier())
              .addMethod(METHOD_ADD_RECORD)
              .addMethod(METHOD_ADD_AMQPRECORD)
              .addMethod(METHOD_ADD_KAFKA_RECORD)
              .addMethod(METHOD_ADD_SQSRECORD)
              .addMethod(METHOD_TEST)
              .build();
        }
      }
    }
    return result;
  }
}
