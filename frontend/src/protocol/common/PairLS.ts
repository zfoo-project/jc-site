import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class PairLS {
    key: number = 0;
    value: string = '';
}

export class PairLSRegistration implements IProtocolRegistration<PairLS> {
    protocolId(): number {
        return 113;
    }

    write(buffer: IByteBuffer, packet: PairLS | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.key);
        buffer.writeString(packet.value);
    }

    read(buffer: IByteBuffer): PairLS | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new PairLS();
        const result0 = buffer.readLong();
        packet.key = result0;
        const result1 = buffer.readString();
        packet.value = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default PairLS;