import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class GetUserProfileRequest {
    
}

export class GetUserProfileRequestRegistration implements IProtocolRegistration<GetUserProfileRequest> {
    protocolId(): number {
        return 754;
    }

    write(buffer: IByteBuffer, packet: GetUserProfileRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
    }

    read(buffer: IByteBuffer): GetUserProfileRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new GetUserProfileRequest();
        
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default GetUserProfileRequest;