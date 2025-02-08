import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import News from './News';


class NewsResponse {
    endId: number = 0;
    news: Array<News> = [];
}

export class NewsResponseRegistration implements IProtocolRegistration<NewsResponse> {
    protocolId(): number {
        return 204;
    }

    write(buffer: IByteBuffer, packet: NewsResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.endId);
        buffer.writePacketList(packet.news, 200);
    }

    read(buffer: IByteBuffer): NewsResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new NewsResponse();
        const result0 = buffer.readLong();
        packet.endId = result0;
        const list1 = buffer.readPacketList(200);
        packet.news = list1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default NewsResponse;