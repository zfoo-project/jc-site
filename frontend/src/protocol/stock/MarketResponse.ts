import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import Market from './Market';


class MarketResponse {
    markets: Array<Market> = [];
}

export class MarketResponseRegistration implements IProtocolRegistration<MarketResponse> {
    protocolId(): number {
        return 304;
    }

    write(buffer: IByteBuffer, packet: MarketResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacketList(packet.markets, 300);
    }

    read(buffer: IByteBuffer): MarketResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new MarketResponse();
        const list0 = buffer.readPacketList(300);
        packet.markets = list0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default MarketResponse;