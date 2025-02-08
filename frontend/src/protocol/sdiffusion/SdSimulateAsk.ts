import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import SignalAttachment from '../attachment/SignalAttachment';
import SdSimulateRequest from './SdSimulateRequest';


class SdSimulateAsk {
    requestSid: number = 0;
    request: SdSimulateRequest | null = null;
    attachment: SignalAttachment | null = null;
}

export class SdSimulateAskRegistration implements IProtocolRegistration<SdSimulateAsk> {
    protocolId(): number {
        return 1070;
    }

    write(buffer: IByteBuffer, packet: SdSimulateAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.attachment, 0);
        buffer.writePacket(packet.request, 1050);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): SdSimulateAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new SdSimulateAsk();
        const result0 = buffer.readPacket(0);
        packet.attachment = result0;
        const result1 = buffer.readPacket(1050);
        packet.request = result1;
        const result2 = buffer.readLong();
        packet.requestSid = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default SdSimulateAsk;