import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class NewsStat {
    newsS: number = 0;
    newsA: number = 0;
    newsB: number = 0;
    newsC: number = 0;
    newsD: number = 0;
}

export class NewsStatRegistration implements IProtocolRegistration<NewsStat> {
    protocolId(): number {
        return 10051;
    }

    write(buffer: IByteBuffer, packet: NewsStat | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.newsA);
        buffer.writeInt(packet.newsB);
        buffer.writeInt(packet.newsC);
        buffer.writeInt(packet.newsD);
        buffer.writeInt(packet.newsS);
    }

    read(buffer: IByteBuffer): NewsStat | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new NewsStat();
        const result0 = buffer.readInt();
        packet.newsA = result0;
        const result1 = buffer.readInt();
        packet.newsB = result1;
        const result2 = buffer.readInt();
        packet.newsC = result2;
        const result3 = buffer.readInt();
        packet.newsD = result3;
        const result4 = buffer.readInt();
        packet.newsS = result4;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default NewsStat;