import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class LoginByWeChatRequest {
    
}

export class LoginByWeChatRequestRegistration implements IProtocolRegistration<LoginByWeChatRequest> {
    protocolId(): number {
        return 705;
    }

    write(buffer: IByteBuffer, packet: LoginByWeChatRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
    }

    read(buffer: IByteBuffer): LoginByWeChatRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new LoginByWeChatRequest();
        
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default LoginByWeChatRequest;