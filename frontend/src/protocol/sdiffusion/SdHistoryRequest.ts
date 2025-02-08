import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class SdHistoryRequest {
    nonce: number = 0;
}

export class SdHistoryRequestRegistration implements IProtocolRegistration<SdHistoryRequest> {
    protocolId(): number {
        return 1052;
    }

    write(buffer: IByteBuffer, packet: SdHistoryRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.nonce);
    }

    read(buffer: IByteBuffer): SdHistoryRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new SdHistoryRequest();
        const result0 = buffer.readLong();
        packet.nonce = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default SdHistoryRequest;