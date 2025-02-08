import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class StatisticsRequest {
    navigation: number = 0;
}

export class StatisticsRequestRegistration implements IProtocolRegistration<StatisticsRequest> {
    protocolId(): number {
        return 10060;
    }

    write(buffer: IByteBuffer, packet: StatisticsRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.navigation);
    }

    read(buffer: IByteBuffer): StatisticsRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new StatisticsRequest();
        const result0 = buffer.readInt();
        packet.navigation = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default StatisticsRequest;