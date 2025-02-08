import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import News from '../news/News';


class SeoAsk {
    news: News | null = null;
}

export class SeoAskRegistration implements IProtocolRegistration<SeoAsk> {
    protocolId(): number {
        return 860;
    }

    write(buffer: IByteBuffer, packet: SeoAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.news, 200);
    }

    read(buffer: IByteBuffer): SeoAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new SeoAsk();
        const result0 = buffer.readPacket(200);
        packet.news = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default SeoAsk;