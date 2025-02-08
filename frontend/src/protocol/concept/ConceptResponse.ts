import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import Concept from './Concept';


class ConceptResponse {
    concepts: Array<Concept> = [];
    core: string = '';
}

export class ConceptResponseRegistration implements IProtocolRegistration<ConceptResponse> {
    protocolId(): number {
        return 352;
    }

    write(buffer: IByteBuffer, packet: ConceptResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacketList(packet.concepts, 350);
        buffer.writeString(packet.core);
    }

    read(buffer: IByteBuffer): ConceptResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new ConceptResponse();
        const list0 = buffer.readPacketList(350);
        packet.concepts = list0;
        const result1 = buffer.readString();
        packet.core = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default ConceptResponse;