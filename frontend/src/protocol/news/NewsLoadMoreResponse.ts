import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import News from './News';


class NewsLoadMoreResponse {
    startId: number = 0;
    news: Array<News> = [];
}

export class NewsLoadMoreResponseRegistration implements IProtocolRegistration<NewsLoadMoreResponse> {
    protocolId(): number {
        return 208;
    }

    write(buffer: IByteBuffer, packet: NewsLoadMoreResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacketList(packet.news, 200);
        buffer.writeLong(packet.startId);
    }

    read(buffer: IByteBuffer): NewsLoadMoreResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new NewsLoadMoreResponse();
        const list0 = buffer.readPacketList(200);
        packet.news = list0;
        const result1 = buffer.readLong();
        packet.startId = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default NewsLoadMoreResponse;