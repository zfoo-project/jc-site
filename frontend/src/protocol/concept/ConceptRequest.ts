import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class ConceptRequest {
    num: number = 0;
}

export class ConceptRequestRegistration implements IProtocolRegistration<ConceptRequest> {
    protocolId(): number {
        return 351;
    }

    write(buffer: IByteBuffer, packet: ConceptRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.num);
    }

    read(buffer: IByteBuffer): ConceptRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new ConceptRequest();
        const result0 = buffer.readInt();
        packet.num = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default ConceptRequest;