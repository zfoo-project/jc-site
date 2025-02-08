import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import MidUpscaleRequest from './MidUpscaleRequest';


class TransferMidUpscaleAsk {
    requestSid: number = 0;
    request: MidUpscaleRequest | null = null;
}

export class TransferMidUpscaleAskRegistration implements IProtocolRegistration<TransferMidUpscaleAsk> {
    protocolId(): number {
        return 825;
    }

    write(buffer: IByteBuffer, packet: TransferMidUpscaleAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.request, 805);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): TransferMidUpscaleAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferMidUpscaleAsk();
        const result0 = buffer.readPacket(805);
        packet.request = result0;
        const result1 = buffer.readLong();
        packet.requestSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferMidUpscaleAsk;