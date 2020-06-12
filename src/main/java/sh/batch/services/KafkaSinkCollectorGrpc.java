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
    comments = "Source: kafka-sink-collector.proto")
public final class KafkaSinkCollectorGrpc {

  private KafkaSinkCollectorGrpc() {}

  public static final String SERVICE_NAME = "services.KafkaSinkCollector";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<sh.batch.services.AddKafkaSinkRecordRequest,
      sh.batch.services.AddKafkaSinkRecordResponse> METHOD_ADD_RECORD =
      io.grpc.MethodDescriptor.<sh.batch.services.AddKafkaSinkRecordRequest, sh.batch.services.AddKafkaSinkRecordResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "services.KafkaSinkCollector", "AddRecord"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.AddKafkaSinkRecordRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.AddKafkaSinkRecordResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static KafkaSinkCollectorStub newStub(io.grpc.Channel channel) {
    return new KafkaSinkCollectorStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static KafkaSinkCollectorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new KafkaSinkCollectorBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static KafkaSinkCollectorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new KafkaSinkCollectorFutureStub(channel);
  }

  /**
   */
  public static abstract class KafkaSinkCollectorImplBase implements io.grpc.BindableService {

    /**
     */
    public void addRecord(sh.batch.services.AddKafkaSinkRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.AddKafkaSinkRecordResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ADD_RECORD, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_ADD_RECORD,
            asyncUnaryCall(
              new MethodHandlers<
                sh.batch.services.AddKafkaSinkRecordRequest,
                sh.batch.services.AddKafkaSinkRecordResponse>(
                  this, METHODID_ADD_RECORD)))
          .build();
    }
  }

  /**
   */
  public static final class KafkaSinkCollectorStub extends io.grpc.stub.AbstractStub<KafkaSinkCollectorStub> {
    private KafkaSinkCollectorStub(io.grpc.Channel channel) {
      super(channel);
    }

    private KafkaSinkCollectorStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KafkaSinkCollectorStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new KafkaSinkCollectorStub(channel, callOptions);
    }

    /**
     */
    public void addRecord(sh.batch.services.AddKafkaSinkRecordRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.AddKafkaSinkRecordResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ADD_RECORD, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class KafkaSinkCollectorBlockingStub extends io.grpc.stub.AbstractStub<KafkaSinkCollectorBlockingStub> {
    private KafkaSinkCollectorBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private KafkaSinkCollectorBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KafkaSinkCollectorBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new KafkaSinkCollectorBlockingStub(channel, callOptions);
    }

    /**
     */
    public sh.batch.services.AddKafkaSinkRecordResponse addRecord(sh.batch.services.AddKafkaSinkRecordRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ADD_RECORD, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class KafkaSinkCollectorFutureStub extends io.grpc.stub.AbstractStub<KafkaSinkCollectorFutureStub> {
    private KafkaSinkCollectorFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private KafkaSinkCollectorFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KafkaSinkCollectorFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new KafkaSinkCollectorFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<sh.batch.services.AddKafkaSinkRecordResponse> addRecord(
        sh.batch.services.AddKafkaSinkRecordRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ADD_RECORD, getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD_RECORD = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final KafkaSinkCollectorImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(KafkaSinkCollectorImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD_RECORD:
          serviceImpl.addRecord((sh.batch.services.AddKafkaSinkRecordRequest) request,
              (io.grpc.stub.StreamObserver<sh.batch.services.AddKafkaSinkRecordResponse>) responseObserver);
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

  private static final class KafkaSinkCollectorDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return sh.batch.services.KafkaSinkCollectorOuterClass.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (KafkaSinkCollectorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new KafkaSinkCollectorDescriptorSupplier())
              .addMethod(METHOD_ADD_RECORD)
              .build();
        }
      }
    }
    return result;
  }
}
