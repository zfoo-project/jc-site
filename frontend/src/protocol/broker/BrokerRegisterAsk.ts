import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class BrokerRegisterAsk {
    brokerType: number = 0;
}

export class BrokerRegisterAskRegistration implements IProtocolRegistration<BrokerRegisterAsk> {
    protocolId(): number {
        return 850;
    }

    write(buffer: IByteBuffer, packet: BrokerRegisterAsk | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.brokerType);
    }

    read(buffer: IByteBuffer): BrokerRegisterAsk | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new BrokerRegisterAsk();
        const result0 = buffer.readInt();
        packet.brokerType = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default BrokerRegisterAsk;