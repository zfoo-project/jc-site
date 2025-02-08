import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class AdminInfoRequest {
    
}

export class AdminInfoRequestRegistration implements IProtocolRegistration<AdminInfoRequest> {
    protocolId(): number {
        return 10000;
    }

    write(buffer: IByteBuffer, packet: AdminInfoRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
    }

    read(buffer: IByteBuffer): AdminInfoRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new AdminInfoRequest();
        
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default AdminInfoRequest;