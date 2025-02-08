import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class RankRequest {
    num: number = 0;
}

export class RankRequestRegistration implements IProtocolRegistration<RankRequest> {
    protocolId(): number {
        return 402;
    }

    write(buffer: IByteBuffer, packet: RankRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.num);
    }

    read(buffer: IByteBuffer): RankRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new RankRequest();
        const result0 = buffer.readInt();
        packet.num = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default RankRequest;