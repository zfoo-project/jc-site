import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import MidZoomRequest from './MidZoomRequest';


class TransferMidZoomAsk {
    requestSid: number = 0;
    request: MidZoomRequest | null = null;
}

export class TransferMidZoomAskRegistration implements IProtocolRegistration<TransferMidZoomAsk> {
    protocolId(): number {
        return 826;
    }

    write(buffer: IByteBuffer, packet: TransferMidZoomAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.request, 806);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): TransferMidZoomAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferMidZoomAsk();
        const result0 = buffer.readPacket(806);
        packet.request = result0;
        const result1 = buffer.readLong();
        packet.requestSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferMidZoomAsk;