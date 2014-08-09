package de.gandev.modjn.communication;

import de.gandev.modjn.entity.ModbusFrame;
import de.gandev.modjn.entity.ModbusFunction;
import de.gandev.modjn.entity.ModbusHeader;
import de.gandev.modjn.entity.func.ModbusError;
import de.gandev.modjn.entity.func.ReadCoilsRequest;
import de.gandev.modjn.entity.func.ReadCoilsResponse;
import de.gandev.modjn.entity.func.ReadDiscreteInputsRequest;
import de.gandev.modjn.entity.func.ReadDiscreteInputsResponse;
import de.gandev.modjn.entity.func.ReadHoldingRegistersRequest;
import de.gandev.modjn.entity.func.ReadHoldingRegistersResponse;
import de.gandev.modjn.entity.func.ReadInputRegistersRequest;
import de.gandev.modjn.entity.func.ReadInputRegistersResponse;
import de.gandev.modjn.entity.func.WriteMultipleCoilsRequest;
import de.gandev.modjn.entity.func.WriteMultipleCoilsResponse;
import de.gandev.modjn.entity.func.WriteMultipleRegistersRequest;
import de.gandev.modjn.entity.func.WriteMultipleRegistersResponse;
import de.gandev.modjn.entity.func.WriteSingleCoil;
import de.gandev.modjn.entity.func.WriteSingleRegister;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.logging.Logger;

/**
 *
 * @author ares
 */
public abstract class ModbusServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger.getLogger(ModbusServerHandler.class.getSimpleName());

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning(cause.getLocalizedMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ModbusServer.allChannels.remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ModbusServer.allChannels.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();

        if (msg instanceof ModbusFrame) {
            ModbusFrame frame = (ModbusFrame) msg;
            ModbusFunction function = frame.getFunction();

            ModbusFrame responseFrame;
            ModbusFunction response;

            logger.info(function.toString());

            if (function instanceof WriteSingleCoil) {
                WriteSingleCoil request = (WriteSingleCoil) function;

                response = writeSingleCoil(request);
            } else if (function instanceof WriteSingleRegister) {
                WriteSingleRegister request = (WriteSingleRegister) function;

                response = writeSingleRegister(request);
            } else if (function instanceof ReadCoilsRequest) {
                ReadCoilsRequest request = (ReadCoilsRequest) function;

                response = readCoilsRequest(request);
            } else if (function instanceof ReadDiscreteInputsRequest) {
                ReadDiscreteInputsRequest request = (ReadDiscreteInputsRequest) function;

                response = readDiscreteInputsRequest(request);
            } else if (function instanceof ReadInputRegistersRequest) {
                ReadInputRegistersRequest request = (ReadInputRegistersRequest) function;

                response = readInputRegistersRequest(request);
            } else if (function instanceof ReadHoldingRegistersRequest) {
                ReadHoldingRegistersRequest request = (ReadHoldingRegistersRequest) function;

                response = readHoldingRegistersRequest(request);
            } else if (function instanceof WriteMultipleRegistersRequest) {
                WriteMultipleRegistersRequest request = (WriteMultipleRegistersRequest) function;

                response = writeMultipleRegistersRequest(request);
            } else if (function instanceof WriteMultipleCoilsRequest) {
                WriteMultipleCoilsRequest request = (WriteMultipleCoilsRequest) function;

                response = writeMultipleCoilsRequest(request);
            } else {
                response = new ModbusError(function.getFunctionCode(), (short) 1);
            }

            ModbusHeader header = new ModbusHeader(
                    frame.getHeader().getTransactionIdentifier(),
                    frame.getHeader().getProtocolIdentifier(),
                    response.calculateLength(),
                    frame.getHeader().getUnitIdentifier());

            responseFrame = new ModbusFrame(header, response);

            channel.write(responseFrame);
        }
    }

    protected abstract WriteSingleCoil writeSingleCoil(WriteSingleCoil request);

    protected abstract WriteSingleRegister writeSingleRegister(WriteSingleRegister request);

    protected abstract ReadCoilsResponse readCoilsRequest(ReadCoilsRequest request);

    protected abstract ReadDiscreteInputsResponse readDiscreteInputsRequest(ReadDiscreteInputsRequest request);

    protected abstract ReadInputRegistersResponse readInputRegistersRequest(ReadInputRegistersRequest request);

    protected abstract ReadHoldingRegistersResponse readHoldingRegistersRequest(ReadHoldingRegistersRequest request);

    protected abstract WriteMultipleRegistersResponse writeMultipleRegistersRequest(WriteMultipleRegistersRequest request);

    protected abstract WriteMultipleCoilsResponse writeMultipleCoilsRequest(WriteMultipleCoilsRequest request);
}