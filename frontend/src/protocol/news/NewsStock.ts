import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class NewsStock {
    name: string = '';
    code: number = 0;
    price: string = '';
    rise: string = '';
}

export class NewsStockRegistration implements IProtocolRegistration<NewsStock> {
    protocolId(): number {
        return 201;
    }

    write(buffer: IByteBuffer, packet: NewsStock | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.code);
        buffer.writeString(packet.name);
        buffer.writeString(packet.price);
        buffer.writeString(packet.rise);
    }

    read(buffer: IByteBuffer): NewsStock | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new NewsStock();
        const result0 = buffer.readInt();
        packet.code = result0;
        const result1 = buffer.readString();
        packet.name = result1;
        const result2 = buffer.readString();
        packet.price = result2;
        const result3 = buffer.readString();
        packet.rise = result3;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default NewsStock;