import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class NewsRequest {
    query: string = '';
    endId: number = 0;
    level: number = 0;
}

export class NewsRequestRegistration implements IProtocolRegistration<NewsRequest> {
    protocolId(): number {
        return 203;
    }

    write(buffer: IByteBuffer, packet: NewsRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.endId);
        buffer.writeInt(packet.level);
        buffer.writeString(packet.query);
    }

    read(buffer: IByteBuffer): NewsRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new NewsRequest();
        const result0 = buffer.readLong();
        packet.endId = result0;
        const result1 = buffer.readInt();
        packet.level = result1;
        const result2 = buffer.readString();
        packet.query = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default NewsRequest;