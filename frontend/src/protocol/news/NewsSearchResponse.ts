import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import News from './News';


class NewsSearchResponse {
    news: Array<News> = [];
}

export class NewsSearchResponseRegistration implements IProtocolRegistration<NewsSearchResponse> {
    protocolId(): number {
        return 210;
    }

    write(buffer: IByteBuffer, packet: NewsSearchResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacketList(packet.news, 200);
    }

    read(buffer: IByteBuffer): NewsSearchResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new NewsSearchResponse();
        const list0 = buffer.readPacketList(200);
        packet.news = list0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default NewsSearchResponse;