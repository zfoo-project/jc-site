import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class MidUpscaleRequest {
    category: string = '';
    nonce: string = '';
    midjourneyId: number = 0;
}

export class MidUpscaleRequestRegistration implements IProtocolRegistration<MidUpscaleRequest> {
    protocolId(): number {
        return 805;
    }

    write(buffer: IByteBuffer, packet: MidUpscaleRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.category);
        buffer.writeLong(packet.midjourneyId);
        buffer.writeString(packet.nonce);
    }

    read(buffer: IByteBuffer): MidUpscaleRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new MidUpscaleRequest();
        const result0 = buffer.readString();
        packet.category = result0;
        const result1 = buffer.readLong();
        packet.midjourneyId = result1;
        const result2 = buffer.readString();
        packet.nonce = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default MidUpscaleRequest;