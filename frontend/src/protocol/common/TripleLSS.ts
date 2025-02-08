import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class TripleLSS {
    left: number = 0;
    middle: string = '';
    right: string = '';
}

export class TripleLSSRegistration implements IProtocolRegistration<TripleLSS> {
    protocolId(): number {
        return 116;
    }

    write(buffer: IByteBuffer, packet: TripleLSS | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.left);
        buffer.writeString(packet.middle);
        buffer.writeString(packet.right);
    }

    read(buffer: IByteBuffer): TripleLSS | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new TripleLSS();
        const result0 = buffer.readLong();
        packet.left = result0;
        const result1 = buffer.readString();
        packet.middle = result1;
        const result2 = buffer.readString();
        packet.right = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default TripleLSS;