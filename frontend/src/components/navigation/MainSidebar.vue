<!--
* @Component:
* @Maintainer: J.K. Yang
* @Description:
-->
<script setup lang="ts">
import configs from "@/configs";
import MainMenu from "@/components/navigation/MainMenu.vue";
import CustomizationMenu from "@/components/CustomizationMenu.vue";
import { useCustomizeThemeStore } from "@/stores/customizeTheme";
import {useMyStore} from "@/stores/myStore";
import { Icon } from "@iconify/vue";
import _ from "lodash";
const customizeTheme = useCustomizeThemeStore();
const navigation = ref(configs.navigation);
const myStore = useMyStore();

const openGithubSite = () => {
  window.open("https://github.com/zfoo-project/zfoo", "_blank");
};

onMounted(() => {
  scrollToBottom();
});

// https://emoji6.com/emojiall/
const jokes = [
  "🌱韭道至简——不割不散🌱",
  "做一个快乐的韭菜👽️👽️👽️",
  "⚔️爱割才会赢⚔️",
  "躺平的韭菜☘️☘️☘️",
  "韭菜炒鸡蛋男人的加油站✌️",
  "做韭菜🍃躺的快🍃",
  "关爱韭菜，人人有责！",
  "韭菜有生命，刀下请留情🌱",
  "🌲韭菜是氧气的制造工厂",
  "韭菜可爱😜大家爱😘",
  "A股红了，韭菜绿了🙃",
  "韭菜依依，无人怜惜🐶",
  "韭菜荫荫🌷润了我心",
  "☁️白云苍狗，韭菜沉浮",
  "韭菜随风都随风🌪️🌪️🌪️",
  "保护韭菜，从我做起",
  "让韭菜的腰杆永远挺直😇",
  "韭菜盒子：生前-韭菜，死后-盒子",
  "🌼韭菜美如画，文明靠大家🌼",
  "草木绿，花儿笑，空气好",
  "我是一只小小小韭菜，总是长呀长不高",
  "给我一颗韭菜，还你一片森林",
  "🃏理解韭菜，成为韭菜🃏",
  "韭菜的快乐你不懂🌠",
  "⚽️韭菜兴旺，匹夫有责",
  "韭菜微微笑，请您绕一绕",
  "韭月韭日忆山顶兄弟🗻",
  "一将功成韭菜无🎆",
  "打服韭菜的唯一办法就是把根刨了",
  "韭菜真是个很爱出风头的蔬菜",
  "谁说韭菜身材不好，它们才是瘦身的最佳良药!",
  "韭菜真是个狡猾的家伙，它总是偷偷地长了又长，永远没人能割得尽。",
  "韭菜是个不事张扬的美食，默默地融入菜肴中，给你带来惊喜",
  "韭菜是个励志的榜样，它告诉我们即使被割得只剩一根，也能顽强地活下去!",
  "研究发现，榨油出油率最高的是三种东西，排在第三的是菜籽，第二是花生，第一是韭菜！",
  "🌏️韭菜的快乐星球🌏️",
  "夜雨剪春韭，新炊间黄梁。主称会面难，一举累十觞。",
  "苦茶久食羽化。与韭同食，令人体重。——陆羽《茶经》",
  "历遍贵游无此味，韭和春雨笋和糟。——叶绍翁《访隐者》",
  "一畦春韭绿，十里稻花香。——曹雪芹《菱荇鹅儿水》",
  "岂无枫叶吴江句，夜雨何时稻韭黄。——方岳《次韵叶兄云崖》",
  "韭苗水饼姑置之，苦菜黄鸡羹糁滑。——黄庭坚《次韵子瞻春菜》",
  "青丝生菜韭芽黄。银缕染红霜。——谢应芳《风入松》",
  "渐觉东风料峭寒，青蒿黄韭试春盘。——苏轼《送范德孺》",
  "春盘得青韭，腊酒寄黄柑。——苏轼《元祐九年立春》",
  "韭黄照春盘，菰白媚秋菜。——黄庭坚《韭黄照春盘》",
  "如丝苣甲饤春盘，韭叶金黄雪未干。——张耒《春日》",
  "韭芽苣甲初出土，春盘饤斗如棼丝。——张耒《雪中狂言》",
  "夜雨剪春韭，新炊间黄粱。——杜甫《赠卫八处士》",
  "嫩割周颙韭，肥烹鲍照葵。——李商隐《题李上谟壁》",
  "漠漠谁家园，秋韭花初白。——白居易《邓州路中作》",
  "秋韭何青青，药苗数百畦。——高适《宋中遇林虑杨十七山人，因而有别》",
  "夜雨剪残春韭。明日重斟别酒。——辛弃疾《昭君怨》",
  "桔槔一水韭苗肥，快活煞学圃樊迟。——马致远《般涉调·哨遍·半世逢场作戏》",
  "齑斗冬中韭，羹怜远处莼。——元稹《代曲江老人百韵》",
  "黄鸡四五只，青韭两三畦。——释文珦《农户》",
  "饼粥悭鱐脯，醯盐劣韭葱。——方回《息担秋云季》",
  "意所欲往路无远，暖韭早梅政堪剪。——方回《白云山房次韵马道士虚中三首》",
  "初筵供园蔬，咸韭最为美。——方回《邻饮》",
  "健啖晚菘兼早韭，快赏调冰仍雪藕。——方回《记三月十日西湖之游吕留卿主人孟君复方万里》",
  "斗酒适酤至，烹鸡漉韭菹。——方回《久晴十月二十四日五日连雨程以忠来同饮》",
  "韭脆鲜鳞缕，梅芳煮醖斟。——方回《次韵张耕道喜雨见怀兼呈赵宾旸》",
  "南山之北归去来，摘菊剪韭手所栽。——方回《离婺源过古荐注口张村三渡遂登五岭》",
  "春韭夏菘谁复种，绿葵红蓼尚堪羹。——苏泂《金陵杂兴二百首·春韭夏菘谁复种》",
  "踏遍路傍青野韭，白翎飞上李陵台。——朱有燉《元宫词·上都随驾自西回》",
  "班荆不以牙旗从，剪韭都忘草具微。——刘克庄《别我暑竹三百里》",
  "拟拨寒蔬寻早韭，试寻玉树索寒梅。——吴潜《和史司直韵五首·滕翁只道老非才》",
  "西埏里接韭溪流，一篑瓶山古木秋。——朱彝尊《鸳鸯湖棹歌》",
  "浑未辨，黄柑荐酒，更传青韭堆盘？——辛弃疾《汉宫春·立春日》",
  "炊粱剪韭贫聊办，煮饼浇葱病未尝。——林泳《蔬餐》",
  "一畦早韭登春盘，五母黄鸡荐秋黍。——何基《西山孝子吟》",
  "荒园秋露瘦韭叶，色茂春菘甘胜蕨。——张耒《秋蔬》",
  "口里雌黄归品藻，盘中韭白想风流。——杨亿《怀寄枢密王左丞》",
  "夜客留同饮，添杯翦韭畦。——赵汝鐩《暮行江上》",
  "秋茄采更稀，夜韭剪仍殖。——徐贲《菜薖为永嘉余唐卿右司赋》",
  "芥薑杞菊，韭薤蒜葫。——邵桂子《疏屋诗为曹云西作》",
  "葵蓼饫颙，葱韭厌徐。——邵桂子《疏屋诗为曹云西作》",
  "公私连樯休，森如束春韭。——黄庭坚《庚寅乙未犹泊大雷口》",
  "带泥烧笋荐晨饷，踏雨剪韭供春虀。——洪咨夔《送新婺州汪总领归歙》",
  "日射韭畦朝可剪，香凝棕室昼常扃。——吴宽《怀修竹书隐》",
  "我见被人瞒，一似园中韭。——寒山《诗三百三首》",
  "早韭晚菘辈，吾家所售用。——周端臣《奉谢芸居清供之招》",
  "人倦马亦疲，剪韭炊黄粱。——吴伟业《遇南厢园叟感赋八十韵》",
  "寒素每叹濡之儒，品俎常嗟韭之九。——曹勋《和李判院见贻》",
  "开樽荐瓜韭，汲井具鸡黍。——李存《赠胡巡检民》",
  "起来烂煮饱枯肠，尽却韭葅并豆粥。——吴芾《又和谢良辅惠箭筍》",
  "早韭欲争春，晚菘先破寒。——苏轼《和陶西田获早稻》",
  "晨饭每同三韭陋，夜棋常办百筹输。——陈造《寄高邮僧》",
  "春韭采来胜市食，秋鲈留取续山肴。——高翥《投老》",
  "匀和豌豆揉葱白，细剪蒌蒿点韭黄。——耶律楚材《十七日驿中作穷春》",
  "平章春韭秋菘味，拆补天吴紫凤图。——陆游《自笑·自笑谋生事事疏》",
  "桂魄元非月蛾靳，韭葅终笑庾郎悭。——陈造《十四夜丞和章来复次韵》",
  "安时从此甘蓣藿，傲世讵敢厌韭葱。——徐贲《次韵王止仲见寄并柬郡诸友》",
  "归而谋妇得斗酒，夜韭可剪鱼可叉。——曹彦约《春日春陪韩签虞丞解监修禊事即席中有虾菜梨》",
  "正有一箪元自足，纵嘲三韭亦何伤？——陆游《对食·天赋元无满谷羊》",
  "薄饭频葅韭，单衣旋制綀。——陆游《幽居·社结山林友》",
  "幸然一命存，微贱不敌韭。——宋濂《予奉诏总裁元史故人操公琬实与纂修寻以病归》",
  "乘凉劝奴婢，园里耨葱韭。——卢仝《寄男抱孙》",
  "风纫晚佩猗兰弱，雨滴春蔬早韭香。——杨基《春日白门写怀用高季迪韵（五首）·绿发无多白发长》",
  "辛盘得青韭，腊酒是黄柑。——苏轼《立春日小集呈李端叔》",
  "燕堂淡薄无歌舞，鲑菜清贫只韭葱。——黄庭坚《和答王世弼》",
  "朝游北里暮南邻，烹鸡剪韭欢悰新。——孙绪《杨师文骢马行春图》",
  "春韭与秋菘，岁晚不供厨。——麻九畴《复次韵二首·其一》",
  "嘉蔬烹笋韭，异味杂螺蛏。——陈高《同诸友游宴丰山》",
  "谁言庾郎贫，未觉三韭乏。——方岳《畦菜》",
  "山盘荐细韭，涧水烹新茶。——李裕《早发胶州往沂与叶方伯马上一日耳目所闻见各成古诗》",
  "绿针挦露韭，碧箸翦春蒿。——喻良能《次韵外舅黄虞卿为爱山园好八首》",
];

const myJoke = jokes[_.random(0, jokes.length - 1)];
const version = myStore.announce.version;

const scrollToBottom = () => {
  const contentArea = document.querySelector(".v-navigation-drawer__content");
  const activeItem = document.querySelector(
    ".v-list-item--active"
  ) as HTMLElement;

  setTimeout(() => {
    contentArea?.scrollTo({
      top: activeItem?.offsetTop,
    });
  }, 100);
};
</script>

<template>
  <v-navigation-drawer
    border="none"
    elevation="1"
    v-model="customizeTheme.mainSidebar"
    id="mainMenu"
  >
    <!-- ---------------------------------------------- -->
    <!---Top Area -->
    <!-- ---------------------------------------------- -->
    <template v-if="!customizeTheme.miniSidebar" v-slot:prepend>
      <v-card
        style="box-shadow: rgba(0, 0, 0, 0.05) 0px 25px 15px -20px"
        height="100"
        class="d-flex align-center justify-center"
      >
        <img
          v-if="customizeTheme.darkTheme"
          width="200"
          src="@/assets/logo_dark.svg"
          alt=""
        />
        <img
          v-else="customizeTheme.darkTheme"
          width="200"
          src="@/assets/logo_light.svg"
          alt=""
        />
      </v-card>
    </template>
    <v-divider />
    <!-- ---------------------------------------------- -->
    <!---Nav List -->
    <!-- ---------------------------------------------- -->

    <main-menu :menu="navigation.menu"></main-menu>

    <!-- ---------------------------------------------- -->
    <!---Bottom Area -->
    <!-- ---------------------------------------------- -->
    <!-- ---------------------------------------------- -->
    <!---Bottom Area -->
    <!-- ---------------------------------------------- -->
    <template v-if="!customizeTheme.miniSidebar" v-slot:append>
      <v-card theme="dark" class="pa-3" variant="text" style="box-shadow: rgba(0, 0, 0, 0.05)">
        <v-card class="d-flex flex-column gradient pa-2" :class="customizeTheme.primaryColor.colorName">
          <v-card-title>
            <v-btn class="mr-2" size="40" color="white" :class="`text-${customizeTheme.primaryColor.colorName}`" icon>
              <Icon width="30" icon="line-md:github-loop" />
            </v-btn>
            jiucai.fun
          </v-card-title>
          <v-card-subtitle class="py-0 my-0"></v-card-subtitle>
          <v-card-text>
            {{ myJoke}}
          </v-card-text>
          <v-card-subtitle class="text-right py-0 my-0">{{ version }}</v-card-subtitle>
          <v-card-actions class="py-0 my-0">
            <v-btn color="white" block prepend-icon="mdi-thumb-up-outline" variant="elevated" @click="openGithubSite">
              Star-Me
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-card>
    </template>
  </v-navigation-drawer>
  <CustomizationMenu />
</template>

<style scoped lang="scss"></style>
