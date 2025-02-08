import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import MidImagineRequest from './MidImagineRequest';


class TransferMidImagineAsk {
    requestSid: number = 0;
    request: MidImagineRequest | null = null;
}

export class TransferMidImagineAskRegistration implements IProtocolRegistration<TransferMidImagineAsk> {
    protocolId(): number {
        return 820;
    }

    write(buffer: IByteBuffer, packet: TransferMidImagineAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.request, 800);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): TransferMidImagineAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferMidImagineAsk();
        const result0 = buffer.readPacket(800);
        packet.request = result0;
        const result1 = buffer.readLong();
        packet.requestSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferMidImagineAsk;