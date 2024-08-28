package com.tartayadir.cryptoservice.e2e;


import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static com.tartayadir.cryptoservice.service.impl.NatsService.SAVE_MESSAGE_SUBJECT;
import static com.tartayadir.cryptoservice.util.StringAssertHelper.assertIdAndKeyFormat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecretAgencyMessageServiceE2ETest {

    private static DockerComposeContainer<?> composeContainer;
    @Autowired
    private Connection natsConnection;
    private static final String TEST_SAVE_RELY_TO_SUBJECT = "save_test.msg";
    private static final String TEST_RECEIVE_RELY_TO_SUBJECT = "receive_test.msg";

    private String currentMessage; //TODO replace with better solution

    @BeforeAll
    public static void setUp() {
        composeContainer = new DockerComposeContainer<>(new File("src/test/resources/docker-compose.test.yml"))
                .withExposedService("mysql_1", 3306, Wait.forListeningPort())
                .withExposedService("nats-server_1", 4222, Wait.forListeningPort())
                .withExposedService("secret-agency-message-service_1", 8080, Wait.forListeningPort());
        composeContainer.start();
    }

    @BeforeEach
    public void setDispatcher(){
        Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {});
        dispatcher.subscribe(TEST_SAVE_RELY_TO_SUBJECT, this::handleTestSaveMessage);
        dispatcher.subscribe(TEST_RECEIVE_RELY_TO_SUBJECT, this::handleReceiveMessage);
    }

    @AfterAll
    public static void tearDown() {
        composeContainer.stop();
    }

    @Test
    public void testMessageFlow() {
        testEncryptionAndDecryption("Test");
        testEncryptionAndDecryption("Text");
        testEncryptionAndDecryption("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc,");
        testEncryptionAndDecryption("But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure? On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee");
        testEncryptionAndDecryption("A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring which I enjoy with my whole heart. I am alone, and feel the charm of existence in this spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents. I should be incapable of drawing a single stroke at the present moment; and yet I feel that I never was a greater artist than now. When, while the lovely valley teems with vapour around me, and the meridian sun strikes the upper surface of the impenetrable foliage of my trees, and but a few stray gleams steal into the inner sanctuary, I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth, a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks, and grow familiar with the countless indescribable forms of the insects and flies, then I feel the presence of the Almighty, who formed us in his own image, and the breath");
        testEncryptionAndDecryption("The quick, brown fox jumps over a lazy dog. DJs flock by when MTV ax quiz prog. Junk MTV quiz graced by fox whelps. Bawds jog, flick quartz, vex nymphs. Waltz, bad nymph, for quick jigs vex! Fox nymphs grab quick-jived waltz. Brick quiz whangs jumpy veldt fox. Bright vixens jump; dozy fowl quack. Quick wafting zephyrs vex bold Jim. Quick zephyrs blow, vexing daft Jim. Sex-charged fop blew my junk TV quiz. How quickly daft jumping zebras vex. Two driven jocks help fax my big quiz. Quick, Baz, get my woven flax jodhpurs! \"Now fax quiz Jack!\" my brave ghost pled. Five quacking zephyrs jolt my wax bed. Flummoxed by job, kvetching W. zaps Iraq. Cozy sphinx waves quart jug of bad milk. A very bad quack might jinx zippy fowls. Few quips galvanized the mock jury box. Quick brown dogs jump over the lazy fox. The jay, pig, fox, zebra, and my wolves quack! Blowzy red vixens fight for a quick jump. Joaquin Phoenix was gazed by MTV for luck. A wizard’s job is to vex chumps quickly in fog. Watch \"Jeopardy!\", Alex Trebek's fun TV quiz game. Woven silk pyjamas exchanged for blue quartz. Brawny gods just");
        //100 symbols
        testEncryptionAndDecryption("pZhUb&9v&j]XWUpFKDZXvj$L@:8,NCS}PM28KeenCi6:4#Eu}V(*Q[4X7SmZPak1m/}+5FP)inA8-mt%HWUbQL%ecQS%T#*-X_SU");
        testEncryptionAndDecryption(")jrY1{nBkW$k1WePvQ13,%.YY$Y$nxW:/J2.hKcFN[P){hkWU4GG3(TJ7+8G!&=$YUNDhyTZ[Vut/8bMzrZXw2$}F6XWZ4)()w+q\n");
        testEncryptionAndDecryption("zpWcR@t5]/:7BMVD?QhDWyCCLiVGaW;j/=7&tuWUXUA&wqyFZ$g$V4FqSA=0q:X8-:3bf9,.n0&XvM3[b/KP85&J,agvbN8F$VaF");
        //1000 symbols
        testEncryptionAndDecryption("#39tV)WzaAKZ!Gf(y]:&60YH*F%9Q3jZSe+Kkdpi61YhNeT7.V/#+Ax4}U.{dt{n0.Lh:-9[QA:vC2a-%#@ba%Lncqgi;KSD64BEq6S=jL?V5i{1;5=v6Va!h}GS-e]y:Df3UVg,ez-::&f-PEGt0{)j0z-mQD,61bHZDNqfQYZ!2U3][aJVRx9(WD/ae*x.QnA-ArhT9Ei]&yQYdSD3N$[{{V%2WdYvfe[.y](B:c%gwL9}*i/!]yPH((4Z4PA=0g[7iTZ$8%t0TLZF{7fM$68_qia@hP6m_cNjyzz{D28=Y(+uF&:%-K.7%c]qB5hTMbNw$.FEvq:c,biQ/c_Zi7b1wNen2F?K&k$jVh3M_3GRPG?WhjUg_Aw}6HZ7CtM3TFPZA@7HBvW#0!*;jyiQUxkHF%UZn/2pQBZ0Nw,$t}]qV]M$+($56y%#eiyCifGDTC[;H8{}qf:.Lq*:iB_0;m/fcdyv:Wx+Y@0V%fX=XeC?pw%Pcp6Anu,)_L;ME=d.7;u+)3bTG1&2#xmMQd),Snm}zDLR,h9-Fq&_2Dr/d&3?.h}0}Q)b1UwECLm6WYc7jf5ig]9A(AZrG4:tU3n1Rn{pAfaiCbuN(*d?6vd0Pp47(i-w$z:r;EuASmPF%H/iFQz+K@-3ebLJptTYk+9D;9_ZxX)T)@E%J#fBfgY;=aby{}6(Z/pWKk;y&*zi(2H.GU_KJhN:X}h%Mbe0zXj0xgqj43WH]U(.RSE5RLZJG%a5w3S0=B%*7gZN_LTV%p6Npa{{YR_$CP9}@6#FeZeW&Txg!ud@!MB+5ct?KqTPM/ZTTj86eP22*F,z9ccj:,hvjdd38LSWN:2Jd?S0%_Wxki7akW*X%N_{/hBVi}G&H)DS;d#d8;Ch(],P;[*j/n_$,,kG_$xXe8UCCB*f(M,:%c=&m%TCamDP4YudcB%XSZU1.+@$4e-0y=w.X$[D@Ei!2fKL3Xcw:g6%dwdZ#Gj*}W$&#EUG7=Ew-$G--T9L");
        testEncryptionAndDecryption("n@f#gD5pT4WvM4re.rh$PkBE:hqktXU+{?{k2-ubQjZj0}W{09]!SUh_rwj7%;&}?7-HPEdZ{;33J_Xd1$S9z6-tx#}U7-,wr$t[Ma;q[-mG)*nA.7Tb55Rx$+LDG*&DjZ:/bXzMqTP.4&:f5jZycUye(3q/.W1H$vx?Lz-S;79rrwcHg(&F@m($QY[Sx(e(3w@7d41tWpt[W{8jZ?E@FbT2!%h1{n=AtCqv}dYr7%_i+{VU!-y0x7cS)hC2i]QqADexW8N;2SMfZ2J89:MExR8bL?@U/3Zx!Jz4UU-hm}@P/%8_f}2?[aXc1=z[cDgF]zi%uNn(}[1BAk0(X:/jMMqUF3kx*Ct9L4&,-Z!b2VEDrhuUWqatLJ*mt_%b,[,:nELm$md0a=)zRL5Ft9!2#7}9?}?+pMn3UTp5ADrb1-b$-.{?@rB9cE9B,Yz[BKgr!NxvPDw7#SgXq.FNv)X{W3r7jXT)kqP%j0{6y:kd2Sk(LNH?R&aES(cqJUhz&@-RbLzUW[*Eb,Q+p1FmyVUzixi]cSi&]Y0+cP=rx1=PTdYPqBMJ?fTTwvd)C{VqP,R?(Hk6G)0q9#ZXUMv6vYcYvA6[?z,Gwk:4{ZZS!Pa,N{m%#T-x1X$3Edy]F?VRQnhEfPhW-*48ykzw.4uKzi[5FW4QvB5S1@dNMex=RB)E6#Se55C9,7116%!D1X;-_e/rxFk{/A!4D9&{{%*7XN&(e&YLwgVg#wyHY8n$8(DX!.@0]19yK&MR)vbcw;19(!R32;SS(+6+${h-45hqM9mfLr*t}CkLN]!$ekYQki.*GP0M&7J3D5CrJ_+4+!%:9cT3[;,VyGM!iY#XHn+XB1u=TppTh/vWv}UM4K&Mu87m[nkR4xeri_#r2p6()Y}h?)/#E&ghL7c0$:d7]k3KMrR/MSGPE2a0RT9%n5;KggQ2!-[6#8gFNd(p.#xS*PQ}np4/c4Gr,X0ZHZY;Le};U/%/!65.@uEhik(&Si]A*@Vq");
        //5000 symbols
        testEncryptionAndDecryption("8vz$Yigaru=RJudxQiZGUpg,8/DEE0gm[yVtL[SFE)hN0CRW%Yk:#hnDw;]Mm=f*V&,YN.8fr-W(+$i,{wB6#=_tF__dpw.+=E;2r#Gff8m}KG5XpXXg!=CjryQq7Sc%0G+e6.]2_DX7%w;V3e-Feakk{$Whd_}wAg%P&w{m6LuGVM,K1z$N;N%d#MSykPi)bWKuME?xP#rx+M:/C0]2Mw@krt$z8=71g;UemqgkaK4ENm[dR8g_p8W7WXn}24vW6i9*2QS2v/17F74pB/jg0E(NU;]EWu=iCexQvE-/ANwxfBj[ZjJ%CDYY)gX1y}mV.E71BYBW[1}{p/[i2Dac!j!xzH36k=9-r=!di/9,negHg30xpNZ?[+]=hL[h6}H=X$B[Z2ww1cAU27.X#AZ+Mx{A0g=Mkpt{GuXz*V@KfSEqVZ:_q:_x@)];9]j/)DyN7eZc_D]jK,v4NK2r-K/T[:.!=*wb%$f08}C8A/,z0mqhFngx$1)q:%N(.x#aFH?za?gT$5T;g-UShJb5U?Z;:@;nERUgP%2%]7cq8}dN_k?%qA)NR1WkE{qy=1hefDn2hke.[h((A{RjGp*=%eft0MNbSzF9M[v;*R?t)pCKnPa[mMc1+r8Ex??T8n0}L+%@E5!mQ/p+FJdw1%:@hSVdaiEwHGWx/zR#tLwLw]*eH?5VQWa)C2Sx/dHmmku$x6!5EqH}mCmJ:.kbyT$_fY(@RK1P&FbR}SK736vC80eD;GUPr=mxEFA11M}6pu!cWGC!T9KxZ/mey4*6}h!6pgx&6Cr,HeC=3R},33X9G3bBFCtJ#mqJ[WZJ4Kw4i&F24CS!:y8fw#?tM$G4)W#R4n!gVU[Uf}BKe]zQa]:m)pHXpK4pK4wT-5a_yASR[Lj$[W}B82Tw..b_qS,#J8A+XDHYVQwQh%J:Xu7C4j}efe9-M=W#.wXRh]F]D-xH2J}Sbvxy.,k.?f5&3Gu]Rp&!]v%GrP0.FTDeLtcJ{JUzXf#[");
        testEncryptionAndDecryption("1JjJaxB$8$B6e[qr$Y/F)KV7dcM2Na/)rX6G2NHBX(,7&7rLcd:6M5_Grm5qEKcy[333iQQ_Em*zYuYd.x}LGw2t9?8{4_Rew)q{-gmC=wr?RdBV8dUu6ZquaU5Pu2H14B{1[aT!_Pi#r50*xU#epXX/vCt[{}C5B92w0eTMLS{mfd1qL--LFFe)=u(8g.!Jg/453uWP@jiR1)NW]g@vaj9Xpj.F%k!-U9B6(_Ddpfay0MC@@BgrCDhAVVr0FF?]E[9Y/P94RTM4Mbd3J7.qV7i!+Vtn$t=FJZ%w/qRX7Dx*_DVY$PQ]Aw(a[Y_WD%He]fP::[84w0HaP)ydgv%gAymKC9M@T,#%aXk.Y/BL(j*W-[8iEP#$F_KRb{,bJpf#tmzQmp#zm+9tiJJT)PV/nf{.6z{Fe@FjY[B]2a$PPN;*LD5=F*ZV_R6,7p:]-;e{a[h@xMW,;?Up6n?Hej1!gpP*q&QST[u9.C-ZZD/3({xq_v828d1H6Gh;A:Zi?xFFci{VWA*R{j%!#P]cd9Axf@tf{1T}!9:+60Vfh1,MBV4+eKCEzkDX)Af/jv5{]_fzFj6weE6+hL[FqFyDyGJ@mX%8Q+D3mrP.e0L1)HGFV!u1RSHW8[aFV.0Pz-3vwbt&EM@W5Re?yGmC$28{X7Y8cF-:6+E3dmXPqNG,KEti&*{4b=wf,&H]C}TZN?c9A&,8[ip6rbS*Gg!?!KE#QJC??QR%0EDg#Gpfn=h77en5B+}02{ZN67[.EH6ABhAVjg*HX3$Hm?A}HX7HxDbXL.(}]QTGQ_8pE0vUdZL0:?8TX,R87&]ax7NKgM7fy$JW%X@Bm/;41v{;f4!/H@0Yd4(!yDMrV?e@Ukn#]&Wr}E4Kda5)yn)rXTN0MB.p2RmQ_mK/AeUfRB,_K;KeD1XhT+uL]+@3gBi[}?M&81{Z3@SK@SMX$;{Ai.5/Zk[&@=x*,hv!Q_TCuP([GQ63[Unv[3QS-F(]L*V%2-L{5XHWtA{4aVwNDNnWU=HJ{g8iUy-eKy&D/T!!d*H[Vb=Ax0_qn{c*kcL)P*![,M5}KRKrxSC@gVhuSU/+bJdm,NK}LmY9)TJ;W$*_]X0)21xJr_T%z7P,[kZiHmwhW#nx!Lh2aS5{Aaa1&P1L.9e7/-;@G*!*9HFr)GP+MyS{@vb;u!KerWL=vVgkv2TZk7-;fwYDwH?X(gq.gWj{jvZ9i%y9K0{x$kcm}nQTS8ccF)aQD&2B$_yT%eVz,Y,J5TNQUSD;z6xaccF6?MA+Y[M3{_-3?BJ],58RkK04X}Y[wKKNye-pr*8]!yp1muqqBP,4nch?*EC;%3A8DCSM!6c@e7_Pp2Q@[Wzu.cZJ%4-XzjUMM4N@J)fC:#]=B+&d3QwD:RGrEZZ;E$J6q+:}0vti[3R+8)6=STf,-V[uPQZcQv3Q3Z%X@nkZ}Q}0hG(yJzRk$kS[F0me#TA@-:uV9QVQn[*(RLU9P@%b7b&6M8uGJ8udym6&#u_Y*bR70@t@=V_iu;FVTu(CnS7L$q8ijyQcXL6TeUBJ%dShbMQwu0k/6K.r6t{D1Z.m/7Z%N#u1ECMqC/%2h1rm4i9,3VX*KYbnY}gU.[k4M0*K-]Tp-Q5CmShD:??K9+8UD3bx+Q,@cK2-;@ib)xg0M,HS]dCt7X@M07FqM_}$.y=nc%uy(Ce!aU{Ttn/Y%NLauK6=r$v2:]9wYkc+@Za$wc#if}@n?uT+:Xu[85Yk,%1gK&K!&RNf)Q.v:5y,m-@&f=3((7n*]fpxfxQxUAeJ@S8tXNK.y1&QHx?B;Pv?;5F$KVdVSy:;kVmCuLc=:R7/US}@2dKwWRie1p3cJ-&C=@9iPJ(#_U=&ZA}hEM]c#nJ,/ke}]dg_9n6fn_p3W9QxJ1Jity}e897!ASUTq&]EpuBum](p%ah[(}wbDZz][1DPGQ;H{::@U{Ky26-#pSmc9$xRX!WEkv*QS1?U40=8-{dh[hi7GtDb!hc!HagA58btFZpjXgexv(2-x/ctAkwGLur3GB.].xSuz+[*;7Ht6}g(z7_y}gyA!eA9!?%!NctZmYtX$B-5gThN/vxey$xp}!Hc.#YtzuMLL.;+}YR7T.&myqXBL47ZC3j)G)1,kVv[8[e%_p/Tr:1ESyk#vP,]9Uf(%nx5ci-Y@i(eHh#eSb8Wf#VLv9q6ECL}$Gt0@)=:a!KRM/gDkHCM@E*K[REH{8.hH@gvm?73:)i0[B0t?cfp]2CRupc(i5&TpU(8WD[y2EX,62G{zY:.4m{gZ)q0.udQjwgA.c{NTh.eF3dKybcX)5$yXQG9(Qym.;{=%8K!JKKDQ}:@B,{+G_iJd;ztREqfbM5Fxm?VUpeYzn-fFh{73?5xYWvV)k{-=uhck@W}!=rx0qj;1V&5%cG)W$S-:AmcC{F)(S+iq1A1ZXr*jR@[6m:E.[rPt6*V@gEtWK.)1a5dgtK0[5J1Mw6nm61}?KWurib/M+G7[SRRV9,AFS7,U9jjhA%xKan?Nz!c#46Lc2zc#T61}y4ZiD@:p[bR3R]K+?dp8L9gzRj?5YKYS@rvUMzut,B,c-C-c9Vrq0:3j7EFm+wMJNm}7j-8a(.0j)v!qnUkzQQ8The/nKFX@mk7uMZZ2D?2vWn67nnej$yE*pES2m5Y/5vE3}=G:m9$]ZF*6C=wk03W.]ut;3)uZ-?taiKX@u{-8)Y@Qu6k=QVvYtn?!H,,XfwKX_(,*L8A!Vkj6X57=9!4z,!3@%hxprp]xjiHQD!PX332H.H$uFp?#3Cg${18Gd$xApuz24*Y%u-mLiT7{Cqn=bna(zXQKq:y_W2X6U+Q:WRzyp?U55St}@CUp-bUMEfCcL)mMWWCr+Q]=_G-eFggDgS.Rn5i!b}i3DWN/#waLe}[#{F&ufZX=]CFr#rr*v+;QLm$WHA%r&z?UT5np/($3*T=tC%NWGLp!P&@f[9Ch!$j5a*1f?nVwGS3%r?H]27Lm0J,i[-pkhzx+(6ZBUGk:SVDSzy/w9$pt%$vdX7{5iueJ2_Y}qQKxBS(%BD_]6wNNwd)jvxv@:enX#ceSZ:j=cMvqfSEwWzCzK*1YnEfuS?)EpR(@#Ne9+$$aRqn;L:&3{PZaK/!,:hFzQJ%n!GR][DV_YUnAC/R3NLg}[U2N/_}ZTe-D.KR0qTfkgBdRzA)1SWz,.MU@:Y{3hA!{ndZ3}0%Q4GHj8bV,$m9;U/y*H{rDQt7c7RB6hqDRx68a7P;MY}T{S$wfhJ$;1HEd.1(dW+p.F.L%xDyb$P*vxG]:Q8aFyTnZ6Y+A-)}d42wWf[YZuhjAA4bXFwi&!Hgf8V{P?xZ9+rhjkHf,yUqM606+8pTwjAy)-$xk{)kUm9giZf;ue00&,yDWu+d-4x24cn.mxKAzC##0Xu;YtSMxz33&_(:.8jqf].q)B]H@V:1XQHJdh5}_yeu(,p&+th0pja+6y.76RcPKLxwQxj+v3hv7Pi_Y(cu.#aeJ_r$;JJU+vBKbCWNX-*{=$cvgu@hk8]}SfuVe$$njc@b7@LYp(1Vrt%BUGmqBUD4X]MC0H7kQ63f{LH}8C?h#XX?wB0ZABb#0e{{&m;g3J7a/,5J*/TK3!U0*)(NL%HQ@A_vX+BR=iyza[u{,0ye;XvSQ,[=UM,dE+}9k!#yBkX{6*1N8_UNm5agXA@J;_JAU9=#Gt1i(E_6X]@cfcH2Ta*&UvBZ&F*/Li)}{.Z})6mN=6(av:5t:YcxhA2}Rf6Xf9?CSgGBrWu;;?R1b#zUKXi+bY?k_6N4Kq6=#&wzGg4YSmw{-JXzm$@SMqArNY:H$Qng;q1T=y4fz.G0-68RnR/m679q4$Qw;LJw@brBYwu%Wph7j!vd4]q4iiNy,XGF![EQb?W)_7ie0/]Bvwwg=ppfEp%0rrz1phSa,D[9R!@}98_tuhz#=Sp8hK6N5etWB/f;:;{zr4hYF6Lbt!5nGA(i=LM=f2Pgjh9AWurz[0@Gtp5c:a(-$}&DH:kfdA2CFReMS5YeywWtchLk=K89[v1=tkhj-g_G%2*Dt_,9+#TqJv+2[8QK;1CKHz&#C=z%2B.82qT-gh1{DJ4Pra$7R?242xSHaGrRDK.gW{$t1Db#{L9{wxXv2/xvNm#;d.{rd2Tm(?D8ed?(iZTV!@Kk7C{vg#c][p.bBjP+DwA%Qb3mR8,k)E&fezgpE/1n/%Ux4rXAvnbQ#9q3iGak,eZ56HkjnfN8LiR&#1*{1*CrATeG4r0WK%[6zT&jDtTu;8cHPE=b=J_HUJgK35J}34=A9xAf32g+mq2*_-6UpwR@.fXXh+Sn[zpkrYT(-12&-!:Y(p}%*9TxwP]*{f%kW+R_v/5zK,yv6}_Vn=Y*[Eu0QqV[=We)N(gY{yimHj2zL4,M[,Fqg7c%9CZXf]Tnp}08r4:,H4B96:CpAf_9mbBDw}d6YS2cfETZfKi#7*w$t0:mdHm}rT9LP(-1S!6;(C_}txX}uHj,?T35EA@aant+4&?gAKK]]Z?*442UAf*FUbQY5vVyp!u)_0U@V_LWnw/4EQ+,8m}f_D}x6B(Fz@V%*xhY;KXM}ZE/!=;Qj?{GDfSgL]fphD_j_))%CR6*Q(]{=bUNBYA8)]y$L$A-e9_ggN48g[80{QmF3[[/t{3,KR,TGZD=AbNM=z+t,-6c(SXCK!Hd?MC}=$zDQwEM8h[*(bgp[wEXrFBh6KDy+r@9JwM&E&vBzh)B4#ZD=@e!Bnj!%b?=RL8@MHW:zXqD}qY}-Yb)U,b]4.6:eE;Kk5VUJ00Y{.zkZ$b3}2L(RfM.m=*PZ]0}5rc5aF@4cy$,H%titGjKwa:6F&BZcg;NC+=i}QRkMymQc-*)5:dudV_Cfp_0KR?]w7f4tWHKfP1]y+Wc8d83e@nnAbT?,kua!AqLzYu2kpPDdJX;USBU&UgeARrt+tMEUv{MJE[(@fmC)im1?UtL))8&yV2+z%}vtX6U6DWUb5tQ;Y=_=@b3Lv_Zgk!@FXNQY(y[R?;p(q[-9dnWTD:89zx&80;,pE0$!jdf,&)HJJ[3KdF%");
    }

    private void testEncryptionAndDecryption(String message) {
        currentMessage = message;
        natsConnection.publish(SAVE_MESSAGE_SUBJECT, TEST_SAVE_RELY_TO_SUBJECT, message.getBytes(UTF_8));
    }

    private void handleTestSaveMessage(Message msg) {
        String data = new String(msg.getData(), UTF_8);
        assertIdAndKeyFormat(data);
        natsConnection.publish(TEST_RECEIVE_RELY_TO_SUBJECT, data.getBytes(UTF_8));
    }

    private void handleReceiveMessage(Message msg) {
        String data = new String(msg.getData(), UTF_8);
        assertEquals(currentMessage, data);
    }
}

