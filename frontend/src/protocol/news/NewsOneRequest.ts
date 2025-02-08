import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class NewsOneRequest {
    id: number = 0;
}

export class NewsOneRequestRegistration implements IProtocolRegistration<NewsOneRequest> {
    protocolId(): number {
        return 205;
    }

    write(buffer: IByteBuffer, packet: NewsOneRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.id);
    }

    read(buffer: IByteBuffer): NewsOneRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new NewsOneRequest();
        const result0 = buffer.readLong();
        packet.id = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default NewsOneRequest;