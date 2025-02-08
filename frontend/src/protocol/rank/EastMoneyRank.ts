import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class EastMoneyRank {
    code: number = 0;
    name: string = '';
    rankChange: number = 0;
    primary: boolean = false;
}

export class EastMoneyRankRegistration implements IProtocolRegistration<EastMoneyRank> {
    protocolId(): number {
        return 400;
    }

    write(buffer: IByteBuffer, packet: EastMoneyRank | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.code);
        buffer.writeString(packet.name);
        buffer.writeBool(packet.primary);
        buffer.writeInt(packet.rankChange);
    }

    read(buffer: IByteBuffer): EastMoneyRank | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new EastMoneyRank();
        const result0 = buffer.readInt();
        packet.code = result0;
        const result1 = buffer.readString();
        packet.name = result1;
        const result2 = buffer.readBool(); 
        packet.primary = result2;
        const result3 = buffer.readInt();
        packet.rankChange = result3;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default EastMoneyRank;