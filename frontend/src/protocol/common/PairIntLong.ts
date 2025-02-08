import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class PairIntLong {
    key: number = 0;
    value: number = 0;
}

export class PairIntLongRegistration implements IProtocolRegistration<PairIntLong> {
    protocolId(): number {
        return 110;
    }

    write(buffer: IByteBuffer, packet: PairIntLong | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.key);
        buffer.writeLong(packet.value);
    }

    read(buffer: IByteBuffer): PairIntLong | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new PairIntLong();
        const result0 = buffer.readInt();
        packet.key = result0;
        const result1 = buffer.readLong();
        packet.value = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default PairIntLong;