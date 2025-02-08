import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import MidSelectRequest from './MidSelectRequest';


class TransferMidSelectAsk {
    requestSid: number = 0;
    request: MidSelectRequest | null = null;
}

export class TransferMidSelectAskRegistration implements IProtocolRegistration<TransferMidSelectAsk> {
    protocolId(): number {
        return 824;
    }

    write(buffer: IByteBuffer, packet: TransferMidSelectAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.request, 804);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): TransferMidSelectAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferMidSelectAsk();
        const result0 = buffer.readPacket(804);
        packet.request = result0;
        const result1 = buffer.readLong();
        packet.requestSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferMidSelectAsk;