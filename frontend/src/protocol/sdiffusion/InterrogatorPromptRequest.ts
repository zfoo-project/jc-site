import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class InterrogatorPromptRequest {
    image: string = '';
    clip_model_name: string = '';
    mode: string = '';
}

export class InterrogatorPromptRequestRegistration implements IProtocolRegistration<InterrogatorPromptRequest> {
    protocolId(): number {
        return 1003;
    }

    write(buffer: IByteBuffer, packet: InterrogatorPromptRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.clip_model_name);
        buffer.writeString(packet.image);
        buffer.writeString(packet.mode);
    }

    read(buffer: IByteBuffer): InterrogatorPromptRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new InterrogatorPromptRequest();
        const result0 = buffer.readString();
        packet.clip_model_name = result0;
        const result1 = buffer.readString();
        packet.image = result1;
        const result2 = buffer.readString();
        packet.mode = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default InterrogatorPromptRequest;