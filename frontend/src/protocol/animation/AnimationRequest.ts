import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class AnimationRequest {
    nonce: string = '';
    imageUrl: string = '';
    type: string = '';
    prompts: Array<string> = [];
}

export class AnimationRequestRegistration implements IProtocolRegistration<AnimationRequest> {
    protocolId(): number {
        return 1211;
    }

    write(buffer: IByteBuffer, packet: AnimationRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.imageUrl);
        buffer.writeString(packet.nonce);
        buffer.writeStringArray(packet.prompts);
        buffer.writeString(packet.type);
    }

    read(buffer: IByteBuffer): AnimationRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new AnimationRequest();
        const result0 = buffer.readString();
        packet.imageUrl = result0;
        const result1 = buffer.readString();
        packet.nonce = result1;
        const array2 = buffer.readStringArray();
        packet.prompts = array2;
        const result3 = buffer.readString();
        packet.type = result3;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default AnimationRequest;