import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import MidInpaintRequest from './MidInpaintRequest';


class TransferMidInpaintAsk {
    requestSid: number = 0;
    request: MidInpaintRequest | null = null;
}

export class TransferMidInpaintAskRegistration implements IProtocolRegistration<TransferMidInpaintAsk> {
    protocolId(): number {
        return 827;
    }

    write(buffer: IByteBuffer, packet: TransferMidInpaintAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.request, 807);
        buffer.writeLong(packet.requestSid);
    }

    read(buffer: IByteBuffer): TransferMidInpaintAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TransferMidInpaintAsk();
        const result0 = buffer.readPacket(807);
        packet.request = result0;
        const result1 = buffer.readLong();
        packet.requestSid = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TransferMidInpaintAsk;