import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import NewsStock from './NewsStock';
import NewsConcept from './NewsConcept';


class News {
    id: number = 0;
    level: string = '';
    title: string = '';
    content: string = '';
    ctime: string = '';
    stocks: Array<NewsStock> = [];
    concepts: Array<NewsConcept> = [];
    subjects: Array<string> = [];
}

export class NewsRegistration implements IProtocolRegistration<News> {
    protocolId(): number {
        return 200;
    }

    write(buffer: IByteBuffer, packet: News | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacketList(packet.concepts, 202);
        buffer.writeString(packet.content);
        buffer.writeString(packet.ctime);
        buffer.writeLong(packet.id);
        buffer.writeString(packet.level);
        buffer.writePacketList(packet.stocks, 201);
        buffer.writeStringList(packet.subjects);
        buffer.writeString(packet.title);
    }

    read(buffer: IByteBuffer): News | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new News();
        const list0 = buffer.readPacketList(202);
        packet.concepts = list0;
        const result1 = buffer.readString();
        packet.content = result1;
        const result2 = buffer.readString();
        packet.ctime = result2;
        const result3 = buffer.readLong();
        packet.id = result3;
        const result4 = buffer.readString();
        packet.level = result4;
        const list5 = buffer.readPacketList(201);
        packet.stocks = list5;
        const list6 = buffer.readStringList();
        packet.subjects = list6;
        const result7 = buffer.readString();
        packet.title = result7;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default News;