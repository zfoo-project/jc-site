import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class Concept {
    id: number = 0;
    level: string = '';
    title: string = '';
    content: string = '';
    url: string = '';
    ctime: string = '';
}

export class ConceptRegistration implements IProtocolRegistration<Concept> {
    protocolId(): number {
        return 350;
    }

    write(buffer: IByteBuffer, packet: Concept | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.content);
        buffer.writeString(packet.ctime);
        buffer.writeLong(packet.id);
        buffer.writeString(packet.level);
        buffer.writeString(packet.title);
        buffer.writeString(packet.url);
    }

    read(buffer: IByteBuffer): Concept | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new Concept();
        const result0 = buffer.readString();
        packet.content = result0;
        const result1 = buffer.readString();
        packet.ctime = result1;
        const result2 = buffer.readLong();
        packet.id = result2;
        const result3 = buffer.readString();
        packet.level = result3;
        const result4 = buffer.readString();
        packet.title = result4;
        const result5 = buffer.readString();
        packet.url = result5;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default Concept;