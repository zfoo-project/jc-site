import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class Market {
    date: number = 0;
    stockNum: number = 0;
    stockNum0: number = 0;
    stockNumNeg005: number = 0;
    stockNumNeg10: number = 0;
    totalPrice: number = 0;
    marketIndex: number = 0;
    shMarketIndex: number = 0;
    kcMarketIndex: number = 0;
    szMarketIndex: number = 0;
    cyMarketIndex: number = 0;
    bjMarketIndex: number = 0;
    exchange: number = 0;
    amount: number = 0;
    shExchange: number = 0;
    shAmount: number = 0;
    kcExchange: number = 0;
    kcAmount: number = 0;
    szExchange: number = 0;
    szAmount: number = 0;
    cyExchange: number = 0;
    cyAmount: number = 0;
    bjExchange: number = 0;
    bjAmount: number = 0;
}

export class MarketRegistration implements IProtocolRegistration<Market> {
    protocolId(): number {
        return 300;
    }

    write(buffer: IByteBuffer, packet: Market | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.amount);
        buffer.writeLong(packet.bjAmount);
        buffer.writeLong(packet.bjExchange);
        buffer.writeLong(packet.bjMarketIndex);
        buffer.writeLong(packet.cyAmount);
        buffer.writeLong(packet.cyExchange);
        buffer.writeLong(packet.cyMarketIndex);
        buffer.writeLong(packet.date);
        buffer.writeLong(packet.exchange);
        buffer.writeLong(packet.kcAmount);
        buffer.writeLong(packet.kcExchange);
        buffer.writeLong(packet.kcMarketIndex);
        buffer.writeLong(packet.marketIndex);
        buffer.writeLong(packet.shAmount);
        buffer.writeLong(packet.shExchange);
        buffer.writeLong(packet.shMarketIndex);
        buffer.writeInt(packet.stockNum);
        buffer.writeInt(packet.stockNum0);
        buffer.writeInt(packet.stockNumNeg005);
        buffer.writeInt(packet.stockNumNeg10);
        buffer.writeLong(packet.szAmount);
        buffer.writeLong(packet.szExchange);
        buffer.writeLong(packet.szMarketIndex);
        buffer.writeLong(packet.totalPrice);
    }

    read(buffer: IByteBuffer): Market | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new Market();
        const result0 = buffer.readLong();
        packet.amount = result0;
        const result1 = buffer.readLong();
        packet.bjAmount = result1;
        const result2 = buffer.readLong();
        packet.bjExchange = result2;
        const result3 = buffer.readLong();
        packet.bjMarketIndex = result3;
        const result4 = buffer.readLong();
        packet.cyAmount = result4;
        const result5 = buffer.readLong();
        packet.cyExchange = result5;
        const result6 = buffer.readLong();
        packet.cyMarketIndex = result6;
        const result7 = buffer.readLong();
        packet.date = result7;
        const result8 = buffer.readLong();
        packet.exchange = result8;
        const result9 = buffer.readLong();
        packet.kcAmount = result9;
        const result10 = buffer.readLong();
        packet.kcExchange = result10;
        const result11 = buffer.readLong();
        packet.kcMarketIndex = result11;
        const result12 = buffer.readLong();
        packet.marketIndex = result12;
        const result13 = buffer.readLong();
        packet.shAmount = result13;
        const result14 = buffer.readLong();
        packet.shExchange = result14;
        const result15 = buffer.readLong();
        packet.shMarketIndex = result15;
        const result16 = buffer.readInt();
        packet.stockNum = result16;
        const result17 = buffer.readInt();
        packet.stockNum0 = result17;
        const result18 = buffer.readInt();
        packet.stockNumNeg005 = result18;
        const result19 = buffer.readInt();
        packet.stockNumNeg10 = result19;
        const result20 = buffer.readLong();
        packet.szAmount = result20;
        const result21 = buffer.readLong();
        packet.szExchange = result21;
        const result22 = buffer.readLong();
        packet.szMarketIndex = result22;
        const result23 = buffer.readLong();
        packet.totalPrice = result23;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default Market;