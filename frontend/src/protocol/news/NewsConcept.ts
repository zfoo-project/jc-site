import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class NewsConcept {
    name: string = '';
    code: number = 0;
    rise: string = '';
}

export class NewsConceptRegistration implements IProtocolRegistration<NewsConcept> {
    protocolId(): number {
        return 202;
    }

    write(buffer: IByteBuffer, packet: NewsConcept | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.code);
        buffer.writeString(packet.name);
        buffer.writeString(packet.rise);
    }

    read(buffer: IByteBuffer): NewsConcept | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new NewsConcept();
        const result0 = buffer.readInt();
        packet.code = result0;
        const result1 = buffer.readString();
        packet.name = result1;
        const result2 = buffer.readString();
        packet.rise = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default NewsConcept;