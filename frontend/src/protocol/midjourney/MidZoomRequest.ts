import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class MidZoomRequest {
    zoom: string = '';
    nonce: string = '';
    midjourneyId: number = 0;
}

export class MidZoomRequestRegistration implements IProtocolRegistration<MidZoomRequest> {
    protocolId(): number {
        return 806;
    }

    write(buffer: IByteBuffer, packet: MidZoomRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.midjourneyId);
        buffer.writeString(packet.nonce);
        buffer.writeString(packet.zoom);
    }

    read(buffer: IByteBuffer): MidZoomRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new MidZoomRequest();
        const result0 = buffer.readLong();
        packet.midjourneyId = result0;
        const result1 = buffer.readString();
        packet.nonce = result1;
        const result2 = buffer.readString();
        packet.zoom = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default MidZoomRequest;