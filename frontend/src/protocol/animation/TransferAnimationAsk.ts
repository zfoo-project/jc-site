import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import AnimationRequest from './AnimationRequest';


class TransferAnimationAsk {
    requestSid: number = 0;
    request: AnimationRequest | null = null;
}

export class TransferAnimationAskRegistration implements IProtocolRegistration<TransferAnimationAsk> {
    protocolId(): number {
        return 1213;
    }

    write(buffer: IByteBuffer, packet: TransferAnimationAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.request, 1211);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): TransferAnimationAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferAnimationAsk();
        const result0 = buffer.readPacket(1211);
        packet.request = result0;
        const result1 = buffer.readLong();
        packet.requestSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferAnimationAsk;