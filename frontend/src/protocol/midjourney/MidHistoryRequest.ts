import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class MidHistoryRequest {
    nonce: string = '';
}

export class MidHistoryRequestRegistration implements IProtocolRegistration<MidHistoryRequest> {
    protocolId(): number {
        return 801;
    }

    write(buffer: IByteBuffer, packet: MidHistoryRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.nonce);
    }

    read(buffer: IByteBuffer): MidHistoryRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new MidHistoryRequest();
        const result0 = buffer.readString();
        packet.nonce = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default MidHistoryRequest;