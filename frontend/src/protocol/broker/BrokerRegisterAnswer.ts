import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class BrokerRegisterAnswer {
    
}

export class BrokerRegisterAnswerRegistration implements IProtocolRegistration<BrokerRegisterAnswer> {
    protocolId(): number {
        return 851;
    }

    write(buffer: IByteBuffer, packet: BrokerRegisterAnswer | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
    }

    read(buffer: IByteBuffer): BrokerRegisterAnswer | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new BrokerRegisterAnswer();
        
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default BrokerRegisterAnswer;