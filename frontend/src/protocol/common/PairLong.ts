import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class PairLong {
    key: number = 0;
    value: number = 0;
}

export class PairLongRegistration implements IProtocolRegistration<PairLong> {
    protocolId(): number {
        return 111;
    }

    write(buffer: IByteBuffer, packet: PairLong | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.key);
        buffer.writeLong(packet.value);
    }

    read(buffer: IByteBuffer): PairLong | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new PairLong();
        const result0 = buffer.readLong();
        packet.key = result0;
        const result1 = buffer.readLong();
        packet.value = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default PairLong;