import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class MidRerollRequest {
    nonce: string = '';
    midjourneyId: number = 0;
}

export class MidRerollRequestRegistration implements IProtocolRegistration<MidRerollRequest> {
    protocolId(): number {
        return 803;
    }

    write(buffer: IByteBuffer, packet: MidRerollRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.midjourneyId);
        buffer.writeString(packet.nonce);
    }

    read(buffer: IByteBuffer): MidRerollRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new MidRerollRequest();
        const result0 = buffer.readLong();
        packet.midjourneyId = result0;
        const result1 = buffer.readString();
        packet.nonce = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default MidRerollRequest;