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
  public static final io.grpc.MethodDescriptor<sh.batch.services.AddRecordsRequest,
      sh.batch.services.AddRecordsResponse> METHOD_ADD_RECORDS =
      io.grpc.MethodDescriptor.<sh.batch.services.AddRecordsRequest, sh.batch.services.AddRecordsResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "services.GRPCCollector", "AddRecords"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.AddRecordsRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              sh.batch.services.AddRecordsResponse.getDefaultInstance()))
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
    public void addRecords(sh.batch.services.AddRecordsRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.AddRecordsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ADD_RECORDS, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_ADD_RECORDS,
            asyncUnaryCall(
              new MethodHandlers<
                sh.batch.services.AddRecordsRequest,
                sh.batch.services.AddRecordsResponse>(
                  this, METHODID_ADD_RECORDS)))
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
    public void addRecords(sh.batch.services.AddRecordsRequest request,
        io.grpc.stub.StreamObserver<sh.batch.services.AddRecordsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ADD_RECORDS, getCallOptions()), request, responseObserver);
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
    public sh.batch.services.AddRecordsResponse addRecords(sh.batch.services.AddRecordsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ADD_RECORDS, getCallOptions(), request);
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
    public com.google.common.util.concurrent.ListenableFuture<sh.batch.services.AddRecordsResponse> addRecords(
        sh.batch.services.AddRecordsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ADD_RECORDS, getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD_RECORDS = 0;

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
        case METHODID_ADD_RECORDS:
          serviceImpl.addRecords((sh.batch.services.AddRecordsRequest) request,
              (io.grpc.stub.StreamObserver<sh.batch.services.AddRecordsResponse>) responseObserver);
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
              .addMethod(METHOD_ADD_RECORDS)
              .build();
        }
      }
    }
    return result;
  }
}
