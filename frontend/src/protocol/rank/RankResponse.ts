import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import EastMoneyRank from './EastMoneyRank';
import ThsRank from './ThsRank';


class RankResponse {
    eastMoneyRanks: Array<EastMoneyRank> = [];
    thsRanks: Array<ThsRank> = [];
    core: string = '';
}

export class RankResponseRegistration implements IProtocolRegistration<RankResponse> {
    protocolId(): number {
        return 403;
    }

    write(buffer: IByteBuffer, packet: RankResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.core);
        buffer.writePacketList(packet.eastMoneyRanks, 400);
        buffer.writePacketList(packet.thsRanks, 401);
    }

    read(buffer: IByteBuffer): RankResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new RankResponse();
        const result0 = buffer.readString();
        packet.core = result0;
        const list1 = buffer.readPacketList(400);
        packet.eastMoneyRanks = list1;
        const list2 = buffer.readPacketList(401);
        packet.thsRanks = list2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default RankResponse;