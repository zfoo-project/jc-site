import SignalAttachment from './attachment/SignalAttachment';
import { SignalAttachmentRegistration } from './attachment/SignalAttachment';
import GatewayAttachment from './attachment/GatewayAttachment';
import { GatewayAttachmentRegistration } from './attachment/GatewayAttachment';
import UdpAttachment from './attachment/UdpAttachment';
import { UdpAttachmentRegistration } from './attachment/UdpAttachment';
import HttpAttachment from './attachment/HttpAttachment';
import { HttpAttachmentRegistration } from './attachment/HttpAttachment';
import NoAnswerAttachment from './attachment/NoAnswerAttachment';
import { NoAnswerAttachmentRegistration } from './attachment/NoAnswerAttachment';
import Message from './common/Message';
import { MessageRegistration } from './common/Message';
import Error from './common/Error';
import { ErrorRegistration } from './common/Error';
import Heartbeat from './common/Heartbeat';
import { HeartbeatRegistration } from './common/Heartbeat';
import Ping from './common/Ping';
import { PingRegistration } from './common/Ping';
import Pong from './common/Pong';
import { PongRegistration } from './common/Pong';
import PairIntLong from './common/PairIntLong';
import { PairIntLongRegistration } from './common/PairIntLong';
import PairLong from './common/PairLong';
import { PairLongRegistration } from './common/PairLong';
import PairString from './common/PairString';
import { PairStringRegistration } from './common/PairString';
import PairLS from './common/PairLS';
import { PairLSRegistration } from './common/PairLS';
import TripleLong from './common/TripleLong';
import { TripleLongRegistration } from './common/TripleLong';
import TripleString from './common/TripleString';
import { TripleStringRegistration } from './common/TripleString';
import TripleLSS from './common/TripleLSS';
import { TripleLSSRegistration } from './common/TripleLSS';
import News from './news/News';
import { NewsRegistration } from './news/News';
import NewsStock from './news/NewsStock';
import { NewsStockRegistration } from './news/NewsStock';
import NewsConcept from './news/NewsConcept';
import { NewsConceptRegistration } from './news/NewsConcept';
import NewsRequest from './news/NewsRequest';
import { NewsRequestRegistration } from './news/NewsRequest';
import NewsResponse from './news/NewsResponse';
import { NewsResponseRegistration } from './news/NewsResponse';
import NewsOneRequest from './news/NewsOneRequest';
import { NewsOneRequestRegistration } from './news/NewsOneRequest';
import NewsOneResponse from './news/NewsOneResponse';
import { NewsOneResponseRegistration } from './news/NewsOneResponse';
import NewsLoadMoreRequest from './news/NewsLoadMoreRequest';
import { NewsLoadMoreRequestRegistration } from './news/NewsLoadMoreRequest';
import NewsLoadMoreResponse from './news/NewsLoadMoreResponse';
import { NewsLoadMoreResponseRegistration } from './news/NewsLoadMoreResponse';
import NewsSearchRequest from './news/NewsSearchRequest';
import { NewsSearchRequestRegistration } from './news/NewsSearchRequest';
import NewsSearchResponse from './news/NewsSearchResponse';
import { NewsSearchResponseRegistration } from './news/NewsSearchResponse';
import Market from './stock/Market';
import { MarketRegistration } from './stock/Market';
import SaveMarketAsk from './stock/SaveMarketAsk';
import { SaveMarketAskRegistration } from './stock/SaveMarketAsk';
import SaveMarketAnswer from './stock/SaveMarketAnswer';
import { SaveMarketAnswerRegistration } from './stock/SaveMarketAnswer';
import MarketRequest from './stock/MarketRequest';
import { MarketRequestRegistration } from './stock/MarketRequest';
import MarketResponse from './stock/MarketResponse';
import { MarketResponseRegistration } from './stock/MarketResponse';
import Concept from './concept/Concept';
import { ConceptRegistration } from './concept/Concept';
import ConceptRequest from './concept/ConceptRequest';
import { ConceptRequestRegistration } from './concept/ConceptRequest';
import ConceptResponse from './concept/ConceptResponse';
import { ConceptResponseRegistration } from './concept/ConceptResponse';
import EastMoneyRank from './rank/EastMoneyRank';
import { EastMoneyRankRegistration } from './rank/EastMoneyRank';
import ThsRank from './rank/ThsRank';
import { ThsRankRegistration } from './rank/ThsRank';
import RankRequest from './rank/RankRequest';
import { RankRequestRegistration } from './rank/RankRequest';
import RankResponse from './rank/RankResponse';
import { RankResponseRegistration } from './rank/RankResponse';
import ChatgptMessageRequest from './chatgpt/ChatgptMessageRequest';
import { ChatgptMessageRequestRegistration } from './chatgpt/ChatgptMessageRequest';
import ChatgptMessageNotice from './chatgpt/ChatgptMessageNotice';
import { ChatgptMessageNoticeRegistration } from './chatgpt/ChatgptMessageNotice';
import ChatgptForceStopRequest from './chatgpt/ChatgptForceStopRequest';
import { ChatgptForceStopRequestRegistration } from './chatgpt/ChatgptForceStopRequest';
import ChatgptForceStopResponse from './chatgpt/ChatgptForceStopResponse';
import { ChatgptForceStopResponseRegistration } from './chatgpt/ChatgptForceStopResponse';
import ChatgptMessage from './chatgpt/ChatgptMessage';
import { ChatgptMessageRegistration } from './chatgpt/ChatgptMessage';
import TransferChatgptAsk from './chatgpt/TransferChatgptAsk';
import { TransferChatgptAskRegistration } from './chatgpt/TransferChatgptAsk';
import TransferChatgptNotify from './chatgpt/TransferChatgptNotify';
import { TransferChatgptNotifyRegistration } from './chatgpt/TransferChatgptNotify';
import ChatMessage from './chat/ChatMessage';
import { ChatMessageRegistration } from './chat/ChatMessage';
import GroupChatNotice from './chat/GroupChatNotice';
import { GroupChatNoticeRegistration } from './chat/GroupChatNotice';
import GroupChatRequest from './chat/GroupChatRequest';
import { GroupChatRequestRegistration } from './chat/GroupChatRequest';
import GroupHistoryMessageRequest from './chat/GroupHistoryMessageRequest';
import { GroupHistoryMessageRequestRegistration } from './chat/GroupHistoryMessageRequest';
import GroupHistoryMessageResponse from './chat/GroupHistoryMessageResponse';
import { GroupHistoryMessageResponseRegistration } from './chat/GroupHistoryMessageResponse';
import LoginRequest from './auth/LoginRequest';
import { LoginRequestRegistration } from './auth/LoginRequest';
import LoginResponse from './auth/LoginResponse';
import { LoginResponseRegistration } from './auth/LoginResponse';
import OssPolicyRequest from './auth/OssPolicyRequest';
import { OssPolicyRequestRegistration } from './auth/OssPolicyRequest';
import OssPolicyResponse from './auth/OssPolicyResponse';
import { OssPolicyResponseRegistration } from './auth/OssPolicyResponse';
import OssPolicyVO from './auth/OssPolicyVO';
import { OssPolicyVORegistration } from './auth/OssPolicyVO';
import LoginByWeChatRequest from './auth/LoginByWeChatRequest';
import { LoginByWeChatRequestRegistration } from './auth/LoginByWeChatRequest';
import LoginByWeChatResponse from './auth/LoginByWeChatResponse';
import { LoginByWeChatResponseRegistration } from './auth/LoginByWeChatResponse';
import User from './user/User';
import { UserRegistration } from './user/User';
import UserProfileNotice from './user/UserProfileNotice';
import { UserProfileNoticeRegistration } from './user/UserProfileNotice';
import UpdateUserProfileRequest from './user/UpdateUserProfileRequest';
import { UpdateUserProfileRequestRegistration } from './user/UpdateUserProfileRequest';
import UpdateUserProfileResponse from './user/UpdateUserProfileResponse';
import { UpdateUserProfileResponseRegistration } from './user/UpdateUserProfileResponse';
import GetUserProfileRequest from './user/GetUserProfileRequest';
import { GetUserProfileRequestRegistration } from './user/GetUserProfileRequest';
import GetUserProfileResponse from './user/GetUserProfileResponse';
import { GetUserProfileResponseRegistration } from './user/GetUserProfileResponse';
import MidImagineRequest from './midjourney/MidImagineRequest';
import { MidImagineRequestRegistration } from './midjourney/MidImagineRequest';
import MidHistoryRequest from './midjourney/MidHistoryRequest';
import { MidHistoryRequestRegistration } from './midjourney/MidHistoryRequest';
import MidImagineNotice from './midjourney/MidImagineNotice';
import { MidImagineNoticeRegistration } from './midjourney/MidImagineNotice';
import MidRerollRequest from './midjourney/MidRerollRequest';
import { MidRerollRequestRegistration } from './midjourney/MidRerollRequest';
import MidSelectRequest from './midjourney/MidSelectRequest';
import { MidSelectRequestRegistration } from './midjourney/MidSelectRequest';
import MidUpscaleRequest from './midjourney/MidUpscaleRequest';
import { MidUpscaleRequestRegistration } from './midjourney/MidUpscaleRequest';
import MidZoomRequest from './midjourney/MidZoomRequest';
import { MidZoomRequestRegistration } from './midjourney/MidZoomRequest';
import MidInpaintRequest from './midjourney/MidInpaintRequest';
import { MidInpaintRequestRegistration } from './midjourney/MidInpaintRequest';
import TransferMidImagineAsk from './midjourney/TransferMidImagineAsk';
import { TransferMidImagineAskRegistration } from './midjourney/TransferMidImagineAsk';
import TransferMidImagineNotify from './midjourney/TransferMidImagineNotify';
import { TransferMidImagineNotifyRegistration } from './midjourney/TransferMidImagineNotify';
import TransferMidRerollAsk from './midjourney/TransferMidRerollAsk';
import { TransferMidRerollAskRegistration } from './midjourney/TransferMidRerollAsk';
import TransferMidSelectAsk from './midjourney/TransferMidSelectAsk';
import { TransferMidSelectAskRegistration } from './midjourney/TransferMidSelectAsk';
import TransferMidUpscaleAsk from './midjourney/TransferMidUpscaleAsk';
import { TransferMidUpscaleAskRegistration } from './midjourney/TransferMidUpscaleAsk';
import TransferMidZoomAsk from './midjourney/TransferMidZoomAsk';
import { TransferMidZoomAskRegistration } from './midjourney/TransferMidZoomAsk';
import TransferMidInpaintAsk from './midjourney/TransferMidInpaintAsk';
import { TransferMidInpaintAskRegistration } from './midjourney/TransferMidInpaintAsk';
import BrokerRegisterAsk from './broker/BrokerRegisterAsk';
import { BrokerRegisterAskRegistration } from './broker/BrokerRegisterAsk';
import BrokerRegisterAnswer from './broker/BrokerRegisterAnswer';
import { BrokerRegisterAnswerRegistration } from './broker/BrokerRegisterAnswer';
import SeoAsk from './broker/SeoAsk';
import { SeoAskRegistration } from './broker/SeoAsk';
import StableDiffusionRequest from './sdiffusion/StableDiffusionRequest';
import { StableDiffusionRequestRegistration } from './sdiffusion/StableDiffusionRequest';
import StableDiffusionResponse from './sdiffusion/StableDiffusionResponse';
import { StableDiffusionResponseRegistration } from './sdiffusion/StableDiffusionResponse';
import StableDiffusionParameters from './sdiffusion/StableDiffusionParameters';
import { StableDiffusionParametersRegistration } from './sdiffusion/StableDiffusionParameters';
import InterrogatorPromptRequest from './sdiffusion/InterrogatorPromptRequest';
import { InterrogatorPromptRequestRegistration } from './sdiffusion/InterrogatorPromptRequest';
import InterrogatorPromptResponse from './sdiffusion/InterrogatorPromptResponse';
import { InterrogatorPromptResponseRegistration } from './sdiffusion/InterrogatorPromptResponse';
import SdSimulateRequest from './sdiffusion/SdSimulateRequest';
import { SdSimulateRequestRegistration } from './sdiffusion/SdSimulateRequest';
import SdSimulateNotice from './sdiffusion/SdSimulateNotice';
import { SdSimulateNoticeRegistration } from './sdiffusion/SdSimulateNotice';
import SdHistoryRequest from './sdiffusion/SdHistoryRequest';
import { SdHistoryRequestRegistration } from './sdiffusion/SdHistoryRequest';
import SdSimulateResponse from './sdiffusion/SdSimulateResponse';
import { SdSimulateResponseRegistration } from './sdiffusion/SdSimulateResponse';
import SdImage from './sdiffusion/SdImage';
import { SdImageRegistration } from './sdiffusion/SdImage';
import ImageDownloadRequest from './sdiffusion/ImageDownloadRequest';
import { ImageDownloadRequestRegistration } from './sdiffusion/ImageDownloadRequest';
import ImageDownloadResponse from './sdiffusion/ImageDownloadResponse';
import { ImageDownloadResponseRegistration } from './sdiffusion/ImageDownloadResponse';
import ImageDeleteAsk from './sdiffusion/ImageDeleteAsk';
import { ImageDeleteAskRegistration } from './sdiffusion/ImageDeleteAsk';
import SdSimulateAsk from './sdiffusion/SdSimulateAsk';
import { SdSimulateAskRegistration } from './sdiffusion/SdSimulateAsk';
import SdSimulateAnswer from './sdiffusion/SdSimulateAnswer';
import { SdSimulateAnswerRegistration } from './sdiffusion/SdSimulateAnswer';
import TransferSdSimulateNotify from './sdiffusion/TransferSdSimulateNotify';
import { TransferSdSimulateNotifyRegistration } from './sdiffusion/TransferSdSimulateNotify';
import AnimationImage from './animation/AnimationImage';
import { AnimationImageRegistration } from './animation/AnimationImage';
import AnimationRequest from './animation/AnimationRequest';
import { AnimationRequestRegistration } from './animation/AnimationRequest';
import AnimationNotice from './animation/AnimationNotice';
import { AnimationNoticeRegistration } from './animation/AnimationNotice';
import TransferAnimationAsk from './animation/TransferAnimationAsk';
import { TransferAnimationAskRegistration } from './animation/TransferAnimationAsk';
import TransferAnimationNotify from './animation/TransferAnimationNotify';
import { TransferAnimationNotifyRegistration } from './animation/TransferAnimationNotify';
import AdminInfoRequest from './admin/AdminInfoRequest';
import { AdminInfoRequestRegistration } from './admin/AdminInfoRequest';
import AdminInfoResponse from './admin/AdminInfoResponse';
import { AdminInfoResponseRegistration } from './admin/AdminInfoResponse';
import Broadcast from './admin/Broadcast';
import { BroadcastRegistration } from './admin/Broadcast';
import DoBroadcastRequest from './admin/DoBroadcastRequest';
import { DoBroadcastRequestRegistration } from './admin/DoBroadcastRequest';
import DoBroadcastResponse from './admin/DoBroadcastResponse';
import { DoBroadcastResponseRegistration } from './admin/DoBroadcastResponse';
import DeleteBroadcastRequest from './admin/DeleteBroadcastRequest';
import { DeleteBroadcastRequestRegistration } from './admin/DeleteBroadcastRequest';
import DeleteBroadcastResponse from './admin/DeleteBroadcastResponse';
import { DeleteBroadcastResponseRegistration } from './admin/DeleteBroadcastResponse';
import Statistics from './admin/Statistics';
import { StatisticsRegistration } from './admin/Statistics';
import NewsStat from './admin/NewsStat';
import { NewsStatRegistration } from './admin/NewsStat';
import StatisticsRequest from './admin/StatisticsRequest';
import { StatisticsRequestRegistration } from './admin/StatisticsRequest';
import IByteBuffer from "./IByteBuffer";
import IProtocolRegistration from "./IProtocolRegistration";

const protocols = new Map<number, IProtocolRegistration<unknown>>();
const protocolIdMap = new Map<any, number>();

// initProtocol
protocols.set(0, new SignalAttachmentRegistration());
protocolIdMap.set(SignalAttachment, 0);
protocols.set(2, new GatewayAttachmentRegistration());
protocolIdMap.set(GatewayAttachment, 2);
protocols.set(3, new UdpAttachmentRegistration());
protocolIdMap.set(UdpAttachment, 3);
protocols.set(4, new HttpAttachmentRegistration());
protocolIdMap.set(HttpAttachment, 4);
protocols.set(5, new NoAnswerAttachmentRegistration());
protocolIdMap.set(NoAnswerAttachment, 5);
protocols.set(100, new MessageRegistration());
protocolIdMap.set(Message, 100);
protocols.set(101, new ErrorRegistration());
protocolIdMap.set(Error, 101);
protocols.set(102, new HeartbeatRegistration());
protocolIdMap.set(Heartbeat, 102);
protocols.set(103, new PingRegistration());
protocolIdMap.set(Ping, 103);
protocols.set(104, new PongRegistration());
protocolIdMap.set(Pong, 104);
protocols.set(110, new PairIntLongRegistration());
protocolIdMap.set(PairIntLong, 110);
protocols.set(111, new PairLongRegistration());
protocolIdMap.set(PairLong, 111);
protocols.set(112, new PairStringRegistration());
protocolIdMap.set(PairString, 112);
protocols.set(113, new PairLSRegistration());
protocolIdMap.set(PairLS, 113);
protocols.set(114, new TripleLongRegistration());
protocolIdMap.set(TripleLong, 114);
protocols.set(115, new TripleStringRegistration());
protocolIdMap.set(TripleString, 115);
protocols.set(116, new TripleLSSRegistration());
protocolIdMap.set(TripleLSS, 116);
protocols.set(200, new NewsRegistration());
protocolIdMap.set(News, 200);
protocols.set(201, new NewsStockRegistration());
protocolIdMap.set(NewsStock, 201);
protocols.set(202, new NewsConceptRegistration());
protocolIdMap.set(NewsConcept, 202);
protocols.set(203, new NewsRequestRegistration());
protocolIdMap.set(NewsRequest, 203);
protocols.set(204, new NewsResponseRegistration());
protocolIdMap.set(NewsResponse, 204);
protocols.set(205, new NewsOneRequestRegistration());
protocolIdMap.set(NewsOneRequest, 205);
protocols.set(206, new NewsOneResponseRegistration());
protocolIdMap.set(NewsOneResponse, 206);
protocols.set(207, new NewsLoadMoreRequestRegistration());
protocolIdMap.set(NewsLoadMoreRequest, 207);
protocols.set(208, new NewsLoadMoreResponseRegistration());
protocolIdMap.set(NewsLoadMoreResponse, 208);
protocols.set(209, new NewsSearchRequestRegistration());
protocolIdMap.set(NewsSearchRequest, 209);
protocols.set(210, new NewsSearchResponseRegistration());
protocolIdMap.set(NewsSearchResponse, 210);
protocols.set(300, new MarketRegistration());
protocolIdMap.set(Market, 300);
protocols.set(301, new SaveMarketAskRegistration());
protocolIdMap.set(SaveMarketAsk, 301);
protocols.set(302, new SaveMarketAnswerRegistration());
protocolIdMap.set(SaveMarketAnswer, 302);
protocols.set(303, new MarketRequestRegistration());
protocolIdMap.set(MarketRequest, 303);
protocols.set(304, new MarketResponseRegistration());
protocolIdMap.set(MarketResponse, 304);
protocols.set(350, new ConceptRegistration());
protocolIdMap.set(Concept, 350);
protocols.set(351, new ConceptRequestRegistration());
protocolIdMap.set(ConceptRequest, 351);
protocols.set(352, new ConceptResponseRegistration());
protocolIdMap.set(ConceptResponse, 352);
protocols.set(400, new EastMoneyRankRegistration());
protocolIdMap.set(EastMoneyRank, 400);
protocols.set(401, new ThsRankRegistration());
protocolIdMap.set(ThsRank, 401);
protocols.set(402, new RankRequestRegistration());
protocolIdMap.set(RankRequest, 402);
protocols.set(403, new RankResponseRegistration());
protocolIdMap.set(RankResponse, 403);
protocols.set(500, new ChatgptMessageRequestRegistration());
protocolIdMap.set(ChatgptMessageRequest, 500);
protocols.set(501, new ChatgptMessageNoticeRegistration());
protocolIdMap.set(ChatgptMessageNotice, 501);
protocols.set(502, new ChatgptForceStopRequestRegistration());
protocolIdMap.set(ChatgptForceStopRequest, 502);
protocols.set(503, new ChatgptForceStopResponseRegistration());
protocolIdMap.set(ChatgptForceStopResponse, 503);
protocols.set(504, new ChatgptMessageRegistration());
protocolIdMap.set(ChatgptMessage, 504);
protocols.set(510, new TransferChatgptAskRegistration());
protocolIdMap.set(TransferChatgptAsk, 510);
protocols.set(511, new TransferChatgptNotifyRegistration());
protocolIdMap.set(TransferChatgptNotify, 511);
protocols.set(600, new ChatMessageRegistration());
protocolIdMap.set(ChatMessage, 600);
protocols.set(601, new GroupChatNoticeRegistration());
protocolIdMap.set(GroupChatNotice, 601);
protocols.set(602, new GroupChatRequestRegistration());
protocolIdMap.set(GroupChatRequest, 602);
protocols.set(603, new GroupHistoryMessageRequestRegistration());
protocolIdMap.set(GroupHistoryMessageRequest, 603);
protocols.set(604, new GroupHistoryMessageResponseRegistration());
protocolIdMap.set(GroupHistoryMessageResponse, 604);
protocols.set(700, new LoginRequestRegistration());
protocolIdMap.set(LoginRequest, 700);
protocols.set(701, new LoginResponseRegistration());
protocolIdMap.set(LoginResponse, 701);
protocols.set(702, new OssPolicyRequestRegistration());
protocolIdMap.set(OssPolicyRequest, 702);
protocols.set(703, new OssPolicyResponseRegistration());
protocolIdMap.set(OssPolicyResponse, 703);
protocols.set(704, new OssPolicyVORegistration());
protocolIdMap.set(OssPolicyVO, 704);
protocols.set(705, new LoginByWeChatRequestRegistration());
protocolIdMap.set(LoginByWeChatRequest, 705);
protocols.set(706, new LoginByWeChatResponseRegistration());
protocolIdMap.set(LoginByWeChatResponse, 706);
protocols.set(750, new UserRegistration());
protocolIdMap.set(User, 750);
protocols.set(751, new UserProfileNoticeRegistration());
protocolIdMap.set(UserProfileNotice, 751);
protocols.set(752, new UpdateUserProfileRequestRegistration());
protocolIdMap.set(UpdateUserProfileRequest, 752);
protocols.set(753, new UpdateUserProfileResponseRegistration());
protocolIdMap.set(UpdateUserProfileResponse, 753);
protocols.set(754, new GetUserProfileRequestRegistration());
protocolIdMap.set(GetUserProfileRequest, 754);
protocols.set(755, new GetUserProfileResponseRegistration());
protocolIdMap.set(GetUserProfileResponse, 755);
protocols.set(800, new MidImagineRequestRegistration());
protocolIdMap.set(MidImagineRequest, 800);
protocols.set(801, new MidHistoryRequestRegistration());
protocolIdMap.set(MidHistoryRequest, 801);
protocols.set(802, new MidImagineNoticeRegistration());
protocolIdMap.set(MidImagineNotice, 802);
protocols.set(803, new MidRerollRequestRegistration());
protocolIdMap.set(MidRerollRequest, 803);
protocols.set(804, new MidSelectRequestRegistration());
protocolIdMap.set(MidSelectRequest, 804);
protocols.set(805, new MidUpscaleRequestRegistration());
protocolIdMap.set(MidUpscaleRequest, 805);
protocols.set(806, new MidZoomRequestRegistration());
protocolIdMap.set(MidZoomRequest, 806);
protocols.set(807, new MidInpaintRequestRegistration());
protocolIdMap.set(MidInpaintRequest, 807);
protocols.set(820, new TransferMidImagineAskRegistration());
protocolIdMap.set(TransferMidImagineAsk, 820);
protocols.set(821, new TransferMidImagineNotifyRegistration());
protocolIdMap.set(TransferMidImagineNotify, 821);
protocols.set(823, new TransferMidRerollAskRegistration());
protocolIdMap.set(TransferMidRerollAsk, 823);
protocols.set(824, new TransferMidSelectAskRegistration());
protocolIdMap.set(TransferMidSelectAsk, 824);
protocols.set(825, new TransferMidUpscaleAskRegistration());
protocolIdMap.set(TransferMidUpscaleAsk, 825);
protocols.set(826, new TransferMidZoomAskRegistration());
protocolIdMap.set(TransferMidZoomAsk, 826);
protocols.set(827, new TransferMidInpaintAskRegistration());
protocolIdMap.set(TransferMidInpaintAsk, 827);
protocols.set(850, new BrokerRegisterAskRegistration());
protocolIdMap.set(BrokerRegisterAsk, 850);
protocols.set(851, new BrokerRegisterAnswerRegistration());
protocolIdMap.set(BrokerRegisterAnswer, 851);
protocols.set(860, new SeoAskRegistration());
protocolIdMap.set(SeoAsk, 860);
protocols.set(1000, new StableDiffusionRequestRegistration());
protocolIdMap.set(StableDiffusionRequest, 1000);
protocols.set(1001, new StableDiffusionResponseRegistration());
protocolIdMap.set(StableDiffusionResponse, 1001);
protocols.set(1002, new StableDiffusionParametersRegistration());
protocolIdMap.set(StableDiffusionParameters, 1002);
protocols.set(1003, new InterrogatorPromptRequestRegistration());
protocolIdMap.set(InterrogatorPromptRequest, 1003);
protocols.set(1004, new InterrogatorPromptResponseRegistration());
protocolIdMap.set(InterrogatorPromptResponse, 1004);
protocols.set(1050, new SdSimulateRequestRegistration());
protocolIdMap.set(SdSimulateRequest, 1050);
protocols.set(1051, new SdSimulateNoticeRegistration());
protocolIdMap.set(SdSimulateNotice, 1051);
protocols.set(1052, new SdHistoryRequestRegistration());
protocolIdMap.set(SdHistoryRequest, 1052);
protocols.set(1053, new SdSimulateResponseRegistration());
protocolIdMap.set(SdSimulateResponse, 1053);
protocols.set(1054, new SdImageRegistration());
protocolIdMap.set(SdImage, 1054);
protocols.set(1055, new ImageDownloadRequestRegistration());
protocolIdMap.set(ImageDownloadRequest, 1055);
protocols.set(1056, new ImageDownloadResponseRegistration());
protocolIdMap.set(ImageDownloadResponse, 1056);
protocols.set(1057, new ImageDeleteAskRegistration());
protocolIdMap.set(ImageDeleteAsk, 1057);
protocols.set(1070, new SdSimulateAskRegistration());
protocolIdMap.set(SdSimulateAsk, 1070);
protocols.set(1071, new SdSimulateAnswerRegistration());
protocolIdMap.set(SdSimulateAnswer, 1071);
protocols.set(1072, new TransferSdSimulateNotifyRegistration());
protocolIdMap.set(TransferSdSimulateNotify, 1072);
protocols.set(1200, new AnimationImageRegistration());
protocolIdMap.set(AnimationImage, 1200);
protocols.set(1211, new AnimationRequestRegistration());
protocolIdMap.set(AnimationRequest, 1211);
protocols.set(1212, new AnimationNoticeRegistration());
protocolIdMap.set(AnimationNotice, 1212);
protocols.set(1213, new TransferAnimationAskRegistration());
protocolIdMap.set(TransferAnimationAsk, 1213);
protocols.set(1214, new TransferAnimationNotifyRegistration());
protocolIdMap.set(TransferAnimationNotify, 1214);
protocols.set(10000, new AdminInfoRequestRegistration());
protocolIdMap.set(AdminInfoRequest, 10000);
protocols.set(10001, new AdminInfoResponseRegistration());
protocolIdMap.set(AdminInfoResponse, 10001);
protocols.set(10002, new BroadcastRegistration());
protocolIdMap.set(Broadcast, 10002);
protocols.set(10003, new DoBroadcastRequestRegistration());
protocolIdMap.set(DoBroadcastRequest, 10003);
protocols.set(10004, new DoBroadcastResponseRegistration());
protocolIdMap.set(DoBroadcastResponse, 10004);
protocols.set(10010, new DeleteBroadcastRequestRegistration());
protocolIdMap.set(DeleteBroadcastRequest, 10010);
protocols.set(10011, new DeleteBroadcastResponseRegistration());
protocolIdMap.set(DeleteBroadcastResponse, 10011);
protocols.set(10050, new StatisticsRegistration());
protocolIdMap.set(Statistics, 10050);
protocols.set(10051, new NewsStatRegistration());
protocolIdMap.set(NewsStat, 10051);
protocols.set(10060, new StatisticsRequestRegistration());
protocolIdMap.set(StatisticsRequest, 10060);

class ProtocolManager {
    static getProtocolId(clazz: any): number {
        const protocolId = protocolIdMap.get(clazz);
        if (protocolId === null || protocolId === undefined) {
            throw '[protocol:' + clazz + '] not exist';
        }
        return protocolId;
    }

    static getProtocol(protocolId: number): IProtocolRegistration<unknown> {
        const protocol = protocols.get(protocolId);
        if (protocol === null || protocol === undefined) {
            throw '[protocolId:' + protocolId + '] not exist';
        }
        return protocol;
    }

    static write(buffer: IByteBuffer, packet: any): void {
        const protocolId = ProtocolManager.getProtocolId(packet.constructor);
        buffer.writeShort(protocolId);
        const protocol = ProtocolManager.getProtocol(protocolId);
        protocol.write(buffer, packet);
    }

    static read(buffer: IByteBuffer): any {
        const protocolId = buffer.readShort();
        const protocol = ProtocolManager.getProtocol(protocolId);
        const packet = protocol.read(buffer);
        return packet;
    }
}

export default ProtocolManager;