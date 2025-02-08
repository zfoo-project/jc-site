import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class NewsLoadMoreRequest {
    query: string = '';
    startId: number = 0;
    level: number = 0;
}

export class NewsLoadMoreRequestRegistration implements IProtocolRegistration<NewsLoadMoreRequest> {
    protocolId(): number {
        return 207;
    }

    write(buffer: IByteBuffer, packet: NewsLoadMoreRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.level);
        buffer.writeString(packet.query);
        buffer.writeLong(packet.startId);
    }

    read(buffer: IByteBuffer): NewsLoadMoreRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new NewsLoadMoreRequest();
        const result0 = buffer.readInt();
        packet.level = result0;
        const result1 = buffer.readString();
        packet.query = result1;
        const result2 = buffer.readLong();
        packet.startId = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default NewsLoadMoreRequest;