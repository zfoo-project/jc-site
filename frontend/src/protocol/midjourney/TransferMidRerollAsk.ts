import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import MidRerollRequest from './MidRerollRequest';


class TransferMidRerollAsk {
    requestSid: number = 0;
    request: MidRerollRequest | null = null;
}

export class TransferMidRerollAskRegistration implements IProtocolRegistration<TransferMidRerollAsk> {
    protocolId(): number {
        return 823;
    }

    write(buffer: IByteBuffer, packet: TransferMidRerollAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.request, 803);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): TransferMidRerollAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferMidRerollAsk();
        const result0 = buffer.readPacket(803);
        packet.request = result0;
        const result1 = buffer.readLong();
        packet.requestSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferMidRerollAsk;