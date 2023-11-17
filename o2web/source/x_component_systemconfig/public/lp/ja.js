o2.xApplication.systemconfig.LP = {
    "title": "システム設定",
    "searchKey": "設定項目検索",
    "default": "デフォルト",
    "permissionDenied": "現在のユーザーの権限が不足しています。システム設定にアクセスするには管理者アカウントを使用する必要があります。",

    "yes": "はい",
    "no": "いいえ",
    "uploadInfo": "ファイルをここにドラッグ＆ドロップするか、アップロードをクリックしてください",

    "baseConfig": "基本設定",
    "systemInfo": "システム情報",
    "uiConfig": "UI設定",
    "componentDeploy": "コンポーネントデプロイ",
    "resourceDeploy": "リソースデプロイ",
    "serviceDeploy": "サービスデプロイ",

    "securityConfig": "セキュリティ設定",
    "passwordConfig": "パスワード設定",
    "loginConfig": "ログイン設定",
    "ssoConfig": "SSO ログイン",
    "ternaryManagement": "三元管理",

    "serverConfig": "サーバー設定",
    "serversConfig": "サーバー構成",
    "centerServer": "センターサーバー",
    "appServer": "アプリケーションサーバー",
    "webServer": "Webサーバー",
    "databaseServer": "データベース",
    "storageServer": "ストレージ設定",
    "cacheConfig": "キャッシュ設定",
    "clusterConfig": "クラスター設定",
    "orgConfig": "組織設定",
    "processConfig": "プロセス設定",
    "cloudConfig": "クラウド",
    "dumpConfig": "バックアップ設定",
    "worktimeConfig": "勤務時間設定",

    "messageConfig": "メッセージ設定",
    "msgTypeConfig": "メッセージタイプ",
    "pushConfig": "プッシュ通知",
    "mailConfig": "メール設定",
    "smsConfig": "SMS設定",
    "mqConfig": "メッセージキュー設定",

    "queryIndexConfig": "インデックス",

    "mobileConfig": "モバイル設定",
    "connectConfig": "接続設定",
    "appConfig": "アプリ設定",
    "moduleConfig": "モジュール設定",
    "iconConfig": "アイコン設定",
    "ddConfig": "DingDing統合",
    "wechatConfig": "WeChat統合",
    "welinkConfig": "WeLink統合",
    "appTools": "アプリツール",
    "integrationConfig": "アプリ統合",

    "select": "選択",

    "_systemInfo": {
        "title": "システム基本情報の設定",
		"systemName": "システム名",
		"systemNameInfo": "システム名はログインページとブラウザのタイトルバーに表示されます。",
		"systemSubTitle": "システムサブタイトル",
		"systemSubTitleInfo": "システムサブタイトルはログインページの下部に表示されます。",
		"systemVersion": "現在のシステムバージョン",
		"systemVersionInfo": "現在のシステムバージョン",
		"baseInfo": "基本情報",
		"systemStatus": "システム状態",
		"moduleStatus": "モジュール実行状況",
		"language": "言語環境",
		"languageInfo": "サーバー言語環境の設定",
		"languageValues": {
			"zh-CN": "簡体中文",
			"en": "英語",
			"es": "スペイン語"
		},
		"running": "実行中",
		"stop": "停止中",
		"enable": "有効",
		"server": "サーバー",
		"node": "ノード",
		"serverInfo": "サーバー情報",
		"webServer": "WEBサーバー",
		"appServer": "アプリケーションサーバー",
		"centerServer": "センターサーバー",
		"dataServer": "データベースサービス",
		"storageServer": "ファイルストレージサービス",
		"dataNode": "データベース",
		"databaseUrl": "データベース接続",
		"byModule": "モジュール別",
		"byServer": "サーバー別",
		"storageNode": "ファイルストレージ",

        "serverData": {
			// "exposeJest": "インターフェースドキュメント(exposeJest)",
			"httpProtocol": "httpプロトコル(httpProtocol)",
			"host": "ホスト(host)",
			"port": "ポート(port)",
			"proxyHost": "プロキシホスト(proxyHost)",
			"proxyPort": "プロキシポート(proxyPort)",
			"requestLogEnable": "httpログの有効化",
			"requestLogBodyEnable": "ログの本文を記録",
			"requestLogRetainDays": "ログの保持日数",
			"sslEnable": "SSLの有効化(sslEnable)",
			// "statEnable": "Druidの有効化",

			"cacheSize": "キャッシュサイズ(cacheSize)",
			"includes": "含まれるクラス(includes)",
			"excludes": "除外されるクラス(excludes)",
			"jmxEnable": "JMXの有効化(jmxEnable)",
			"lockTimeout": "テーブルロックタイムアウト(lockTimeout)",
			"logLevel": "ログレベル(logLevel)",
			"maxIdle": "最大アイドル接続数(maxIdle)",
			"maxTotal": "最大接続数(maxTotal)",
			"slowSqlMillis": "遅いSQL閾値(slowSqlMillis)",
			"statFilter": "Druidステートメント結合の有効化(statFilter)",
			"tcpPort": "TCPポート(tcpPort)",
			"webPort": "WEBポート(webPort)"
		},
        "storageData": {
			"port": "ftpポート(port)",
			"sslEnable": "SSL有効化(sslEnable)",
			"name": "名称(name)",
			"passivePorts": "パッシブモードポート(passivePorts)",
			"prefix": "パスプレフィックス(prefix)",
			"deepPath": "ディープパス使用(deepPath)"
		},
		"storageAccounts": {
			"protocol": "プロトコル",
			"username": "モジュール",
			"weight": "ウェイト",
			"name": "名称",
			"prefix": "パスプレフィックス",
			"deepPath": "ディープパス使用",
			"host": "ホスト",
			"port": "ポート"
		},
		"moduleData": {
			"node": "サービスノード",
			"contextPath": "コンテキスト",
			"port": "サービスポート",
			"sslEnable": "SSL有効化",
			"proxyHost": "プロキシホスト",
			"proxyPort": "プロキシポート",
			"reportDate": "前回報告時間",
			"moduleName": "モジュール名",
			"className": "クラス"
		}
    },
    "operation": {
        "edit": "編集",
        "ok": "確認",
        "cancel": "キャンセル",
        "enable": "有効",
        "disable": "無効"
    },
    "_component": {
        "open": "開く",
        "edit": "編集",
        "uninstall": "アンインストール",

        "deploy": "コンポーネントの展開",

        "removeComponentTitle": "コンポーネントのアンインストール確認",
        "removeComponent": "コンポーネント：{name} をアンインストールしてもよろしいですか？",
        "removeComponentOk": "コンポーネントがアンインストールされました",

        "deploySuccess": "コンポーネントの展開に成功しました",

        "selectIcon": "アイコンの選択",
        "clearIcon": "アイコンのクリア",

        "name": "コンポーネント名",
        "title": "コンポーネントタイトル",
        "path": "コンポーネントパス",
        "urlPathInfo": "パスをウェブページのURLとして追加するには、「@url:」を使用できます。例：「@url:http://www.bing.com」",
        "visible": "可視性",
        "allowList": "アクセス許可リスト",
        "denyList": "アクセス拒否リスト",
        "icon": "コンポーネントアイコン",

        "upload": "リソースのアップロード",
        "uploadWarn": "コンポーネントのzipファイルをアップロードすると、既存のコンポーネントが上書きされます。注意して操作してください！",

        "componentDataError": "コンポーネント名、コンポーネントパス、およびコンポーネントタイトルは空にできません"
    },
    "_resource": {
        "webResource": "Webリソースのデプロイ",
        "webResourceInfo": "ここでWebリソースをデプロイできます。静的リソースファイルまたはzipファイルをアップロードし、それをシステムのWebサーバーにデプロイし、HTTPプロトコルでアクセスできます。",
        "serviceResource": "カスタムサービスのデプロイ",
        "serviceResourceInfo": "ここで開発したカスタムプロジェクトをデプロイできます。コンパイル済みのjarファイルまたはwarファイルをアップロードできます。デプロイ後、サーバーを再起動する必要があります。",

        "componentResource": "コンポーネントデプロイ",
        "componentResourceInfo": "ユーザーがカスタマイズしたO2OAコンポーネントまたは公式のコンポーネントをここでデプロイできます。O2OAコンポーネントは「x_component_{コンポーネント名}」というフォルダまたはzipファイルです。詳細情報は<a href='https://www.o2oa.net/develop.html' target='_blank'>O2OA公式コミュニティ</a>をご覧ください。",

        "upload": "リソースのアップロード",
        "webUploadWarn": "デプロイする静的リソースファイルをアップロードします。zipファイルは自動的に展開されます。",
        "serviceUploadWarn": "デプロイするjarファイルまたはwarファイルをアップロードします。",

        "overwrite": "デプロイ方法",
        "overwriteFalse": "削除後にアップロード：同じ名前のファイルとフォルダを削除してからアップロードします。",
        "overwriteTrue": "上書き：同じ名前のファイルとフォルダを直接上書きします。",

        "deployPath": "デプロイパス",
        "deployPathInfo": "zipファイルをデプロイする場合、パスは空白にできます。単一ファイルをデプロイする場合は、必ずデプロイパスを指定してください。例：/myWebResource/subPath",

        "noDeployFile": "デプロイするリソースファイルを先に選択してください。",
        "deploySuccess": "リソースのデプロイに成功しました",

        "notWebResource": "<span style='color: red'>現在のサーバーではフロントエンドのWebリソースデプロイが許可されていません。この機能を有効にするには、サーバー設定 - サーバータスクで設定できます。</span>",
        "notServiceResource": "<span style='color: red'>現在のサーバーではフロントエンドのカスタムサービスデプロイが許可されていません。この機能を有効にするには、サーバー設定 - サーバータスクで設定できます。</span>"
    },
    "_uiConfig": {
        "baseConfig": "基本設定",
        "menuConfig": "メインメニュー設定",
        "lnkConfig": "サイドバー設定",
        "userConfig": "ユーザーインターフェース設定",

        "openStatus": "システムへのアクセス",
        "openStatusInfo": "O2OAシステムにアクセスするたびに、デフォルトでは最後にシステムを終了した際に開いていたアプリケーションが自動的に起動します。この動作を変更することができます。",
        "openStatusCurrent": "開いているアプリケーションと現在のアプリケーションを前回終了したシステムの状態に設定します（デフォルト）",
        "openStatusApp": "前回終了したシステムのアプリケーションを開き、ホームページを現在のアプリケーションとして設定します",
        "openStatusIndex": "ホームページアプリケーションのみを開く",

        "skin": "システムスキン",
        "skinConfig": "システムスキンのカスタマイズを許可するかどうか",
        "skinConfigInfo": "ユーザーがシステムスキンをカスタマイズできるかどうか",
        "skinDefault": "システムのデフォルトスキン",
        "skinDefaultInfo": "システムのデフォルトスキンカラーを設定します",
        "scaleConfig": "拡大縮小を許可するかどうか",
        "scaleConfigInfo": "ユーザーがシステムの表示スケールをカスタマイズできるかどうか",

        "defaultMenuInfo": "デフォルトのメニュー設定として保存すると、個別のメニュー設定を行っていないユーザーに対してこの設定が適用されます。",
        "forceMenuInfo": "強制メニュー設定として保存すると、すべてのユーザーにこの設定が強制的に適用され、個別の設定は無視されます。",
        "userMenuInfo": "すべてのユーザーの個別メニュー設定がクリアされ、デフォルトの方法でメニューが表示されます。",

        "clearDefaultMenuDataTitle": "デフォルトメニュー設定をクリア",
        "clearDefaultMenuData": "デフォルトメニュー設定をクリアしますか？",
        "clearDefaultMenuDataSuccess": "デフォルトメニュー設定がクリアされました",
        "clearForceMenuDataTitle": "強制メニュー設定をクリア",
        "clearForceMenuData": "強制メニュー設定をクリアしますか？",
        "clearForceMenuDataSuccess": "強制メニュー設定がクリアされました",

        "clearUserMenuData": "ユーザーの個別メニュー設定をクリア",
        "clearUserMenuDataSuccess": "ユーザーの個別メニュー設定がクリアされました",
        "clearUserMenuDataConfirm": "すべてのユーザーの個別メニュー設定をクリアしますか？",

        "saveDefaultMenuDataSuccess": "デフォルトメニュー設定が正常に保存されました",
        "saveForceMenuDataSuccess": "強制メニュー設定が正常に保存されました",

        "defaultMenu": "デフォルトメニュー設定",
        "forceMenu": "強制メニュー設定",
        "userMenu": "ユーザー個別メニュー設定",

        "saveMenu": "設定を保存",
        "clearMenu": "設定をクリア",
        "loadMenu": "設定を読み込み",
        "clearUserMenu": "ユーザー設定をクリア",

        "menu": {
            "application": "アプリケーション",
            "process": "プロセス",
            "cms": "情報",
            "query": "データ",

            "defaultMenu": "デフォルトメニューの状態に戻す"
        },
        "deleteLink": "よく使用されるアプリケーションのショートカットを削除"
    },
    "_passwordConfig": {
        "personPassword": "ユーザーパスワード設定",
		"adminPassword": "管理者パスワード",
		"saveSuccess": "設定の保存に成功しました",
		"passwordScript": "パスワードスクリプト",

		"newPersonPassword": "新規ユーザーの初期パスワード",
		"newPersonPasswordInfo": "新しいユーザーを作成する際、以下の設定に従ってユーザーの初期パスワードが生成され、ユーザーはシステムにログインした後、自分で変更できます",
		"initialPassword": "ユーザーの初期パスワード",
		"initialPasswordText": "初期パスワードを入力",
		"initialPasswordTypeOptions": {
			"mobile": "携帯電話番号の後ろ6桁",
			"unique": "一意のコードの後ろ6桁",
			"employee": "従業員番号",
			"pinyin": "従業員の名前のフルスペル",
			"text": "固定のパスワード",
			"script": "スクリプトを使用してカスタムの初期パスワードを生成"
		},
		"initialPasswordType": {
			"mobileScript": "return person.getMobile().slice(-6)",
			"uniqueScript": "return person.getUnique().slice(-6)",
			"employeeScript": "return person.getEmployee()",
			"pinyinScript": "return person.getPinyin()",
			"textInfo": "下の入力欄に入力したパスワードは、新しく作成されたユーザーの初期パスワードとして使用されます。",
			"scriptInfo": "下のエディタにスクリプトを入力し、新しく作成されたユーザーの初期パスワードとして使用する文字列値を返します。personオブジェクトを使用して関連するユーザー情報を取得できます。例えば、従業員の名前のフルスペルを初期パスワードとして使用するには、次のスクリプトを使用できます：return person.getPinyin()"
		},

        "passwordPeriod": "パスワード有効期限日数",
		"passwordPeriodInfo": "パスワードを変更しないまま、設定された日数以上経過したユーザーは、ログイン後にパスワード変更が必要であり、それをしない場合、システムにアクセスできなくなります。0に設定すると、パスワードは期限切れになりません。",

		"passwordRegex": "パスワード複雑性",
		"passwordRegexInfo": "ユーザーパスワードの複雑性要件を設定します",

		"passwordRegexMin": "最小長",
		"passwordRegexMax": "最大長",
		"passwordRegexLength": "パスワード長",
		"passwordRule": "パスワードルール",
		"passwordRuleValue": {
			"useLowercase": "小文字を含む必要があります",
			"useNumber": "数字を含む必要があります",
			"useUppercase": "大文字を含む必要があります",
			"useSpecial": "特殊文字を含む必要があります (#?!@$%^&*-)"
		},
		"passwordRuleRegex": {
			"useLowercase": "(?=.*[a-z])",
			"useNumber": "(?=.*\\d)",
			"useUppercase": "(?=.*[A-Z])",
			"useSpecial": "(?=.*?[#?!@$%^&*-])"
		},
		"savePasswordRule": "パスワードルール設定を保存",
		"passwordLengthText": "{n}文字、{text}",

		"passwordRsa": "パスワードの暗号化伝送",
		"passwordRsaInfo": "システムはデフォルトで平文伝送を使用しています。このオプションを有効にして、パスワードの暗号化伝送を有効にできます。（変更後、サーバーを再起動する必要があります）",


        "adminPasswordInfo": "ここで、スーパーアドミニストレーターxadminのパスワードを変更できます。（変更後にサーバーを再起動する必要があります）",
		"modifyAdminPassword": "管理者パスワードを変更",

		"oldPassword": "元のパスワード",
		"newPassword": "新しいパスワード",
		"confirmPassword": "パスワードの確認",

		"ternaryPassword": "三元管理者のパスワード",
		"ternaryPasswordInfo": "三元管理を有効にした場合、システム管理者（systemManager）、セキュリティ管理者（securityManager）およびセキュリティ監査担当者（auditManager）のパスワードをここで変更できます。",
		"modifySystemManagerPassword": "システム管理者のパスワードを変更",
		"modifySecurityManagerPassword": "セキュリティ管理者のパスワードを変更",
		"modifyAuditManagerPassword": "セキュリティ監査者のパスワードを変更",

		"passwordDisaccord": "新しいパスワードと確認パスワードが一致しません",
		"passwordEmpty": "元のパスワード、新しいパスワード、確認パスワードを入力してください",

		"tokenEncryptType": "パスワード暗号化方式",
		"tokenEncryptTypeInfo": "O2OAは以下のパスワードとトークンの暗号化方式をサポートしており、必要に応じて選択できます。詳細については、次をご覧ください：<a href='https://www.o2oa.net/search.html?q=%E5%9B%BD%E5%AF%86' target='_blank'>国家暗号</a>",
		"tokenEncryptTypeLabel": "暗号化方式",
		"encryptTypeOptions": {
			"default": "デフォルト",
			"sm4": "国家商用暗号アルゴリズム"
		},
		"tokenEncryptTypeInfo3": "<div style='color: red'>注意：「パスワード暗号化方式の変更を確認」をクリックした後、この設定が即座に有効になります。<ul style='line-height: 30px'><li>これにより、すべてのユーザーのログインステータスが無効になります。 2. 暗号化方式が変更されるため、既存のユーザーはシステムにログインできなくなります。</li>" +
			"<li>システムを正常に使用するには、次の手順を実行する必要があります：<br> xadminアカウントを使用してシステムに再ログインし、どの方法であれすべてのユーザーのパスワードをリセットします。</li></ul></div>",
		"tokenEncryptTypeButton": "パスワード暗号化方式を変更を確認",
		"changeTokenEncryptTypeInfo": "パスワード暗号化方式を変更してもよろしいですか？"
    },
    "_loginConfig": {
        "baseConfig": "基本設定",
		"moreConfig": "追加設定",
		"ldapConfig": "LDAP認証設定",
		"captchaLogin": "画像認証コードログインを有効化",
		"codeLogin": "SMS認証コードログインを有効化",
		"bindLogin": "QRコードスキャンログインを有効化",
		"faceLogin": "顔認識ログインを有効化",
		"captchaLoginInfo": "有効化すると、ログイン時に画像認証コードを正しく入力する必要があります。",
		"codeLoginInfo": "有効化すると、SMS認証コードを使用してログインできます。",
		"bindLoginInfo": "有効化すると、QRコードをスキャンしてログインできます。",
		"faceLoginInfo": "有効化すると、顔認識ログインを許可し、ユーザーは個人設定で顔の特徴を設定できます。有効化するには、faceという名前のSSO設定を作成し、キーをxplatformに設定する必要があります（これは実験的な機能で、httpsを有効にする必要があります）",

		"loginError": "ログインエラー処理",
		"loginErrorInfo": "ユーザーが連続して複数回誤ったパスワードを入力した場合、アカウントがロックされます。ここで連続ログインエラーの上限回数とアカウントのロック期間を設定できます。",

		"loginErrorCount": "ログインエラー回数上限",
		"lockTime": "ロック時間（分）",

		"tokenExpired": "ログイン有効期間",
		"tokenExpiredInfo": "ユーザーがシステムにログインした後、一定時間サーバーとの対話がない場合、システムはそのログインをログアウトします。ここでログインの有効期間を分単位で設定できます。",

		"tokenName": "トークン名",
		"tokenNameInfo": "システムのデフォルトトークン名はx-tokenですが、同一ドメイン内でクッキーの競合を防ぐために、ここでトークン名を変更できます。これは同一ドメインに複数のO2OAを展開する場合に特に役立ちます。（サーバーを再起動する必要があります）",

		"tokenCookieHttpOnly": "クッキーHttpOnlyを有効化",
		"tokenCookieHttpOnlyInfo": "トークンのクッキーを保存するときにHttpOnlyを有効化するかどうか",

		"tokenCookieSecure": "クッキーセキュアを有効化",
		"tokenCookieSecureInfo": "トークンのクッキーを保存するときにセキュアを有効化し、このクッキーはhttpsプロトコルでのみ送信されるようにします。",

		"enableSafeLogout": "セキュアログアウトを有効化",
		"enableSafeLogoutInfo": "セキュアログアウトを有効化すると、どの端末からログアウト操作を実行してもすべての端末のログイン状態が同時にログアウトされます。",

		"register": "自己登録を有効化",
		"registerInfo": "ここで、システムユーザーへの自己登録を許可するかどうか、および自己登録の方法を構成できます。",
		"registerValues": {
			"disable": "許可しない",
			"captcha": "認証コードを使用して登録",
			"code": "SMSで登録"
		},

        "loginPage": "ポータルページを使用してログイン",
		"loginPageInfo": "システムはカスタムのポータルページをログインページとして使用することをサポートしており、ログインページアプリケーションテンプレートはアプリケーションマーケットで無料で入手できます。",
		"loginPagePortal": "ポータルログイン",

		"selectPortal": "ポータルを選択",

		"indexPage": "ポータルページをシステムのホームページとして使用",
		"indexPageInfo": "カスタムのポータルページをシステムのホームページとして使用し、ログイン後にこのページが開きます。",
		"indexPagePortal": "ホームページポータル",

		"ldapAuthEnable": "LDAP認証を有効化",
		"ldapAuthEnableInfo": "これを有効にすると、ユーザーログイン認証にLDAP認証を使用し、システムのパスワードは使用されなくなります。以下のLDAPパラメータを正しく設定してください。",
		"ldapAuthUrl": "LDAPアドレス",
		"ldapAuthUrlInfo": "LDAPサービスアドレス、ldap://ドメインまたはIP:ポート",
		"baseDn": "LDAPクエリルート(BaseDN)",
		"baseDnInfo": "LDAPクエリのルート名、例：dc=zone, DC=COM",
		"userDn": "認証ユーザーバインディング属性",
		"userDnInfo": "認証ユーザーバインディング属性：uid、携帯電話番号、従業員コード、またはメールアドレス（baseDn以下で一意であること、およびo2で関連ユーザーが見つかることを確認する必要があります、例：uidまたはmailなど）",

		"superPermission": "スーパー管理者パスワードを有効化",
		"superPermissionInfo": "このオプションを有効にすると、スーパー管理者（xadmin）のパスワードで他のユーザーアカウントにログインできるため、管理者は一般ユーザーとしてデータのメンテナンスやトラブルシューティングを行うことができます。",

		"bindDnUser": "バインド管理ユーザー",
		"bindDnUserInfo": "バインドする管理者（管理権限を持っている必要があります）を指定し、認証のクエリに使用します。例：cn=root",
		"bindDnPwd": "管理者ユーザーパスワード",
		"bindDnPwdInfo": "管理者ユーザーのパスワード",
		"ldapEnabledError": "LDAPパラメータをすべて正しく構成した後、LDAP認証を有効にしてください。"

    },
    "_ssoConfig": {
        "ssoConfig": "認証キーの設定",
		"ssoConfigInfo": "複数のシステムに対してシングルサインオン（SSO）ログインとサービス呼び出しのための認証を作成できます。",
		"ssoConfigInfo2": "各認証には認証名とキーを提供する必要があり、このキーはアクセスチケットを生成するための暗号化および復号化の公開鍵です。",
		"addSSOConfig": "認証設定を追加",
		"editSSOConfig": "認証設定を編集",
		"isEnable": "有効化するか",
		"ssoConfigName": "認証名",
		"ssoConfigKey": "キー",

		"ssoConfigKeyInfo": "キーの長さは8の倍数である必要があります。",
		"ssoKeyLengthError": "キーの長さを8の倍数に保持してください。",

		"removeSSOConfigTitle": "認証設定の削除の確認",
		"removeSSOConfig": "認証設定を削除してもよろしいですか：“{name}”",

		"ssoDataError": "認証名と認証キーを入力してください。",
		"ssoSameNameError": "認証名 “{name}” はすでに存在します。別の名前を使用してください。",

        "useSSOConfig": "認証キーの使用方法",
		"useSSOConfigInfo": "認証キーは、次の2つのシナリオで使用する必要があります：",
		"useSSOConfigInfo1": "1. 外部システムがO2OAとシングルサインオンを実装する必要がある場合;",
		"useSSOConfigInfo2": "2. 外部システムがO2OAプラットフォームのインターフェースサービスを呼び出す必要がある場合;",
		"useSSOConfigInfo3": "認証の名前とキーを外部システムに伝える必要があり、外部システムは3DESアルゴリズムを使用してキーで<span style='color: blue'>\"person#timestamp\"</span>テキストを暗号化し、O2OAへの一時的なチケット（トークン）を取得します。<br/>" +
			"<span style='color: blue'>person</span>: 特定のユーザーのユーザー名、ユニークコード、または従業員番号を示します。（具体的には、外部システムとO2OAのユーザー関連フィールドに依存します。）<br/>" +
			"<span style='color: blue'>timestamp</span>: 1970年1月1日0時0分0秒から現在までのミリ秒数を示します。（トークンの有効期限を保証するために、有効期間は1分です。）<br/><br>" +
			"トークンを生成した後、外部システムは次のアドレスにアクセスすることで、O2OAとのシングルサインオンを実現できます：<br/>" +
			"http://servername/x_desktop/sso.html?client={<span style='color: blue'>client</span>}&xtoken={<span style='color: blue'>token</span>}&redirect={<span style='color: blue'>redirect</span>}<br/>" +
			"<span style='color: blue'>client</span>: 使用中の認証名を示します。<br/>" +
			"<span style='color: blue'>token</span>: 生成された一時的なチケットトークンを示します。<br/>" +
			"<span style='color: blue'>redirect</span>: 認証が成功した後に移動するアドレスを示します。<br/>",
		"useSSOConfigInfo4": "認証構成に関する詳細情報は、<a target='_blank' href='https://www.o2oa.net/search.html?q=%E9%89%B4%E6%9D%83'>こちらをクリックして確認してください</a>。",

        "ssoTokenTools": "関連ツール",
		"ssoTokenCode": "暗号化サンプルコードを表示",
		"ssoTokenCheck": "トークンの有効性を検証",

		"oauthConfig": "OAuth設定",
		"oauthClientConfig": "OAuthクライアント設定",
		"oauthServerConfig": "OAuthサーバー設定",

		"oauthClientConfigInfo": "O2OAプラットフォームをOAuth2認証サーバーとして使用する場合、ここで複数のOAuthクライアントを構成して、他のシステムに対するログインと認証を実装できます。",
		"oauthServerConfigInfo": "既にOAuth2認証サーバーがある場合、ここで複数のOAuthサーバーを構成して、このシステムに対するログインと認証を実現できます。",

		"addOauthClientConfig": "OAuthクライアント設定を追加",
		"addOauthServerConfig": "OAuthサーバー設定を追加",
		"editOauthClientConfig": "OAuthクライアントを編集",
		"editOauthServerConfig": "OAuthサーバーを編集",

		"removeOauthConfigTitle": "OAuth設定の削除確認",
		"removeOauthConfig": "OAuth設定を削除してもよろしいですか？：“{name}”",

		"oauthClientDataError": "クライアントID(ClientId)とクライアントシークレット(ClientSecret)は空にできません。",
		"oauthClientSameNameError": "クライアントID(ClientId) “{name}”が既に存在します。他のクライアントIDを使用してください。",

        "oauth_clientId": "クライアントID",
		"oauth_clientSecret": "クライアントシークレット",
		"oauth_mapping": "リターンマッピング",
		"oauth_name": "名前",
		"oauth_displayName": "表示名",
		"oauth_icon": "アイコンURL",
		"oauth_authAddress": "鍵リクエストアドレス",
		"oauth_authParameter": "鍵リクエストパラメータ",
		"oauth_authMethod": "鍵リクエスト方法",

		"oauth_tokenAddress": "トークンリクエストアドレス",
		"oauth_tokenParameter": "トークンリクエストパラメータ",
		"oauth_tokenMethod": "トークンリクエスト方法",
		"oauth_tokenType": "トークンフォーマット",

		"oauth_infoAddress": "情報リクエストアドレス",
		"oauth_infoParameter": "情報リクエストパラメータ",
		"oauth_infoMethod": "情報リクエスト方法",
		"oauth_infoType": "情報フォーマット",

		"oauth_infoCredentialField": "個人情報フィールド",
		"oauth_bindingField": "バインディングユーザーフィールド",

		"oauth_infoScriptText": "情報処理スクリプト",

		"infoScriptTextInfo": "情報フォーマットがJSONまたはFORMでない場合、スクリプトを使用して情報をJSONオブジェクトにフォーマットし、システムが正しく処理できるようにすることができます。下記のスクリプトエディタでスクリプトを記述し、JSONオブジェクトを返します。 <span style='color: blue'>this.text</span> を使用して、応答情報の元のテキストを取得できます。"

    },
    "_ternaryManagement": {
        "enable": "三者管理の有効化",
		"enableInfo": "システムは、システム管理者、セキュリティ管理者、セキュリティ監査担当者の三者がシステムセキュリティ管理を分担し、権限を分散させる方法をサポートしており、三者管理を有効にすると、xadminユーザーと権限が解除され、システムの監査ログ記録が有効になります（サーバーの再起動が必要）。<br>" +
			"三者それぞれの役割分担は次の通りです： " +
			"<ul><li>システム管理者（システム組み込みユーザー：systemManager）：システムユーザー、組織管理、システムの実行と保守作業を担当します。 </li>" +
			"<li>セキュリティ管理者（システム組み込みユーザー：securityManager）：権限設定を担当し、システム監査ログ、ユーザー、およびシステム管理者の操作行動を審査および分析します。 </li>" +
			"<li>セキュリティ監査担当者（システム組み込みユーザー：auditManager）：システム管理者、セキュリティ管理者の操作行動を監査およびトラッキングします。 </li></ul>" +
			"アプリケーションは毎日午前1時に前日の操作ログを分析し、三つの管理者が監査および検索できるようにします。<br>" +
			"三者管理機能を完全に使用するには、「三者管理」アプリをアプリケーションマーケットからインストールする必要があります。" +
			"三者管理に関する詳細な情報は、以下の文書およびビデオをご覧ください：<a href='https://www.o2oa.net/search.html?q=%E4%B8%89%E5%91%98%E7%AE%A1%E7%90%86' target='_blank'>三者管理</a>",
		"logRetainDays": "ログの保持日数",
		"logRetainDaysInfo": "ログを最大で保持する日数を設定します。",

		"logBodyEnable": "ボディ内容の記録",
		"logBodyEnableInfo": "ボディ内容を記録すると、詳細なログ情報が得られますが、ディスクスペースの使用量とサーバーの負荷が大幅に増加する可能性があります。"
    },
    "_databaseServer": {
        "databaseSource": "データソースの設定",
		"entity": "エンティティクラスの設定",
		"tools": "バックアップツール",
		"infoInner": "O2OAの組み込みデータベースを使用しています。O2OAの組み込みデータベースは埋め込み型のメモリデータベースで、開発環境や機能デモ環境に適していますが、正式な環境としては適していません。 " +
			"正式な環境で使用する場合は、より高性能で安定した商用レベルのデータベースを使用することをお勧めします。",
		"infoExternal": "拡張データベースをすでに使用しています。O2OAの組み込みデータベースは無効になっています。",

		"info": "<span style='color: red'>データベース設定を変更すると、ほとんどの場合、既存のデータに影響を及ぼす可能性があります。この設定を慎重に変更してください！</span>",
		"info2": "データベース設定を変更する前に、システムデータをバックアップするためにO2OAのバックアップ機能（ctl -dd）を使用することをお勧めします。データベース設定を変更した後、サーバーを再起動し、バックアップデータをデータベースに復元する必要があります（ctl -rd）。すべてのデータベース関連の設定変更にはサーバーの再起動が必要です。",

		"innerDataSources": "組み込みデータベース",
		"externalDataSources": "拡張データベース",
		"innerDataSourcesInfo": "O2OAの組み込みデータベースは埋め込み型のメモリデータベースで、開発環境や機能デモ環境に適しています。",
		"externalDataSourcesInfo": "O2OAは外部データベースの拡張をサポートしており、プロダクション環境ではデータのセキュリティとパフォーマンスを確保するために商用レベルのデータベースを使用することをお勧めします。",

		"addDatabaseConfig": "データベース設定の追加",

		"databaseUrl": "データベース接続",
		"enable": "有効にする",
		"username": "ユーザー名",
		"password": "パスワード",

        "tcpPort": "接続ポート",
		"tcpPortInfo": "データベースのJDBC接続ポートで、ユーザー名はsa、初期データベースパスワードはxadminです。データベースは/o2server/local/repository/data/X.mv.dbに作成されます。データベースファイルが作成されると、そのデータベースのパスワードも作成されます。",
		"webPort": "WEBポート",
		"webPortInfo": "H2はWebベースのクライアントを提供し、このポートはWebクライアントのアクセスポートです。ユーザー名はsa、初期のxadminデータベースパスワードです。",
		"jmxEnable": "JMX有効化",
		"jmxEnableInfo": "有効にすると、ローカルJMXクライアントを介してアクセスでき、リモートJMXクライアントはサポートされません。",
		"cacheSize": "キャッシュサイズ",
		"cacheSizeInfo": "H2データベースのキャッシュサイズで、M単位でメモリサイズを設定します。デフォルトは512Mです。",
		"logLevel": "ログレベル",
		"maxTotal": "最大接続数",
		"maxIdle": "最大アイドル接続数",
		"statEnable": "統計情報有効化",
		"statFilter": "統計情報フィルタ",
		"slowSqlMillis": "遅いSQLミリ秒",
		"slowSqlMillisInfo": "遅いSQLを個別に記録するための基準時間（デフォルト: 2000ミリ秒）",
		"lockTimeout": "ロックタイムアウト（ミリ秒）",

        "inputDatabaseUrl": "データベース接続情報を入力してください",

		"entityConfig": "エンティティクラスのストレージ割り当て",
		"entityConfigInfo": "複数のデータベースを有効にした場合、ここでシステム内のエンティティクラスのストレージを割り当てて性能を向上させることができます。<span style='color: red'>すべてのエンティティクラスに対応するストレージデータベースを割り当てたことを確認する必要があります。</span>",

		"oneDatabase": "システム内のエンティティクラスにストレージデータベースを割り当てるには、2つ以上のデータベースを有効にする必要があります。現在、1つのデータベースしか有効になっていません。",
		"oneDatabaseInfo": "システム内のエンティティクラスにストレージデータベースを割り当てるには、2つ以上のデータベースを有効にする必要があります。",

		"includeEntity": "許可されたエンティティクラス",
		"includeEntityInfo": "このデータベースでストレージを許可するエンティティクラスのリストです。すべてを許可する場合は空にしてください。複数のエンティティクラスを入力する場合は、カンマまたは改行で区切ります。",
		"excludeEntity": "除外されたエンティティクラス",
		"excludeEntityInfo": "このデータベースでストレージを禁止するクラスのリストです。何も制限しない場合は空にしてください。複数のエンティティクラスを入力する場合は、カンマまたは改行で区切ります。",

		"editDatabase": "データベース設定の編集",


        "saveDatabaseConfig": "すべてのデータベース設定を保存",
		"saveDatabaseConfigInfo": "このページの設定はすぐには保存されません。変更した設定を保存するには、このボタンをクリックする必要があります。",
		"saveDatabaseConfirm": "データベース設定を保存しようとしています。<br><span style='color:red'>既存のデータ（ビジネスデータおよびデザインデータを含む）に影響を及ぼす可能性があります。</span><br><br>データベース設定を保存しますか？",

		"reloadDatabaseConfig": "すべてのデータベース設定をリロード",
		"reloadDatabaseConfigInfo": "保存されていない変更を破棄したい場合は、このボタンをクリックして設定を再読み込みできます。",
		"reloadDatabaseConfirm": "データベース設定を再読み込みしようとしています。保存されていない変更は失われる可能性があります。データベース設定を再読み込みしますか？",

		"saveEntityConfig": "エンティティ設定を保存",
		"saveEntityConfirm": "エンティティ設定を保存しようとしています。<br><span style='color:red'>既存のデータ（ビジネスデータおよびデザインデータを含む）に影響を及ぼす可能性があります。</span><br><br>エンティティ設定を保存しますか？",
		"reloadEntityConfig": "エンティティ設定をリロード",
		"reloadEntityConfirm": "エンティティ設定を再読み込みしようとしています。保存されていない変更は失われる可能性があります。エンティティ設定を再読み込みしますか？",

        "entityList": "選択リスト",
		"selectedEntityList": "選択済みリスト",
		"findClass": "クラス名の検索",

		"removeDatabaseConfigTitle": "データベース設定の削除確認",
		"removeDatabaseConfig": "<span style='color: red'>注意：データベース設定 \"{name}\" を削除しようとしています。データベースを削除する前に、必ずシステムデータのバックアップを取ってください。</span><br><br>この操作を実行しますか？",

		"saveDatabaseConfigSuccess": "データベース設定が正常に保存されました。サーバーを再起動してください。",
		"saveEntityConfigSuccess": "エンティティクラスの設定が正常に保存されました。サーバーを再起動してください。",

		"dumpRestoreTools": "データベースバックアップとリストアツール",
		"toolsInfo": "O2OAはデータバックアップとリストアツールを提供しています。 <span style='color: red'>データベース設定を変更すると、ほとんどの場合、既存のシステムデータに影響を与える可能性があるため</span>、" +
			"データベース設定を変更する前に、O2OAのバックアップ機能を使用してシステムデータをバックアップし、データベース設定を変更した後にサーバーを再起動し、バックアップしたデータをデータベースにリストアすることをお勧めします。<br>" +
			"<span class='mainColor_color'>データバックアップまたはリストアの作業中は、このページから離れないでください。他のブラウザウィンドウで他の操作を行うことができます。</span>",

        "dumpTools": "データバックアップ",
		"dumpToolsInfo": "このボタンをクリックしてデータをバックアップします。<span style='color: red'>システムがデータを頻繁に読み書きしている間にバックアップしないでください。</span>",
		"dumpWaitLog": "データバックアップ未実行",
		"dumpErrorLog": "データバックアップ中にエラーが発生しました",

		"dumpBegin": "バックアップ開始確認",
		"dumpBeginInfo": "データバックアップはサーバーのパフォーマンスに影響を与える可能性があるため、データバックアップを開始しますか？",

		"dumpCheckButton": "バックアップステータスを確認",
		"dumpCheck": "バックアップステータスを確認中...",
		"dumpStop": "データバックアップ未実行",
		"dumpRunning": "データバックアップが進行中...",
		"dumpEnd": "データバックアップが完了しました",

        "restoreTools": "データ復元",
		"restoreToolsInfo": "このボタンをクリックしてデータを復元します。<span style='color: red'>システムがデータを頻繁に読み書きしている間に復元しないでください。</span>",
		"restoreToolsInfo2": "システムにデータテーブルが含まれている場合、データ復元を完了した後、データセンターに移動してすべてのデータテーブルをコンパイルし、データ復元を再実行してサーバーを再起動してください。",
		"restoreWaitLog": "データ復元未実行",
		"restoreErrorLog": "データ復元中にエラーが発生しました",

		"restoreBegin": "復元開始確認",
		"restoreBeginInfo": "データ復元はサーバーのパフォーマンスに影響を与える可能性があるため、データ復元を開始しますか？",

		"restoreCheckButton": "復元ステータスを確認",
		"restoreCheck": "復元ステータスを確認中...",
		"restoreStop": "データ復元未実行",
		"restoreRunning": "データ復元が進行中...",
		"restoreEnd": "データ復元が完了しました"


    },
    "_cloudConfig": {
        "info": "O2クラウドサービスは、アプリケーションマーケット、モバイルオフィスロケーション、SMSサービス、ドキュメントコンバージョンなど、多くの付加価値サービスを提供しています。 O2クラウドサーバーにログインするだけで、これらのサービスを利用できます。",
		"recheck": "接続を再確認",

		"notValidatedInfo": "O2クラウドにログインすると、アプリケーションマーケットにアクセスしたり、モバイルオフィスアプリに接続したり、SMSサービスやドキュメントコンバージョンなど、多くの機能を利用できます！",
		"disconnectInfo": "お使いのサーバーはO2クラウドに接続できません。サーバーネットワーク環境を確認してください。",
		"validatedInfo": "<span style='color: #ff0000'>こんにちは：</span>{name}、O2クラウドにログインしました。モバイルオフィスなど、O2プラットフォームのすべての機能をご利用いただけます！",

		"connected": "O2クラウドに接続できるようになりました！",
		"disconnect": "お使いのサーバーはO2クラウドに接続できません！",
		"notValidated": "O2クラウドにログインしていません！",
		"validated": "O2クラウドにログインしました！",

		"loginInfo": "すでにO2クラウドアカウントをお持ちの場合は、こちらをクリックしてログインしてください：",
		"loginButtonText": "O2クラウドにログイン",
		"registerInfo": "O2クラウドアカウントをお持ちでない場合は、こちらをクリックして登録してください：",
		"registerButtonText": "O2クラウドアカウントを登録",
		"forgotPasswordInfo": "O2クラウドアカウントのパスワードを忘れた場合は、こちらをクリックしてリセットしてください：",
		"forgotPasswordButtonText": "O2クラウドのパスワードをリセット",

        "collectUsername": "O2クラウドアカウント",
		"collectPassword": "O2クラウドパスワード",
		"collectMobile": "携帯電話番号",
		"collectMail": "メールアドレス",
		"collectCode": "確認コード",
		"collectConfirm": "パスワードの確認",
		"getCode": "確認コードを取得",
		"regetCode": "再取得",

		"inputCollectUsername": "O2クラウドアカウントを入力してください",
		"inputCollectPassword": "O2クラウドアカウントのパスワードを入力してください",
		"inputCollectMobile": "携帯電話番号を入力してください",
		"inputCollectMail": "メールアドレスを入力してください",
		"inputCollectCode": "SMS確認コードを入力してください",
		"inputCollectConfirm": "パスワードの確認を入力してください",
		"collectUsernameExist": "O2クラウドアカウント名は既に存在します",
		"collectUsernameNotExist": "O2クラウドアカウント名は存在しません",
		"passwordDisagree": "パスワードの確認が一致しません",
		"mobileError": "携帯電話番号が正しくありません",
		"mailError": "メールアドレスが正しくありません",

        "registerCollect": "O2クラウドアカウント登録",
		"forgotPassword": "パスワードを忘れた場合",
		"loginError": "O2クラウドログインに失敗しました。アカウント名とパスワードを確認してください",
		"registerError": "O2クラウドアカウント登録エラー。技術サポートにお問い合わせください",
		"deleteError": "O2クラウドアカウント削除エラー。技術サポートにお問い合わせください",
		"resetPasswordError": "O2クラウドアカウントのパスワード再設定エラー。技術サポートにお問い合わせください",

		"deleteCollectUnit": "O2クラウドアカウント削除",
		"deleteCollectUnitInfo": "O2クラウドアカウントを削除します：{name}、携帯電話番号を入力し、確認のために認証コードを取得してください",

		"resetPasswordCollect": "O2クラウドアカウントのパスワード再設定",

		"modifyCollect": "アカウントの変更",
		"logoutCollect": "接続解除",
		"modifyCollectPassword": "パスワードの変更",
		"deleteCollect": "アカウントの削除",
		"reloginCollect": "再度ログイン"
    },
    "_serversConfig": {
        "serverInfo": "サーバー情報",
		"baseConfig": "基本設定",
		"environmentConfig": "環境変数設定",
		"sameConfig": "同じサーバー設定を使用",
		"sameConfigInfo": "O2OAには中央サービス、アプリケーションサービス、WEBサービスの3つの論理サーバーがあり、デフォルトでは同じポートと同じ設定を使用しますが、これらのサービスに異なるポート、ホストなどを個別に設定することもできます。",

		"serverConfig": "サーバー設定",
		"serverConfigInfo": "ここでサーバー関連のパラメータを設定します（サーバーを再起動する必要があります）",

		"serverPort": "サービスポート",
		"serverPortInfo": "サーバーのリッスンポート",

		"serverProxyHost": "アクセスホスト名",
		"serverProxyPort": "アクセスホストポート",
		"sslEnable": "SSLを有効にする",
		"httpProtocol": "WEBアクセスプロトコル",
		"sslKeyStorePassword": "SSLキーストアパスワード",
		"sslKeyManagerPassword": "SSLキーマネージャーパスワード",
		"sslInfo": "<span>SSLを有効にするには、既に申請済みの証明書ファイルをO2OAサーバーのconfigディレクトリにコピーし、`keystore`という名前に変更する必要があります。クラスター環境では、各サーバーに証明書ファイルを配置する必要があります（サーバーを再起動する必要があります）</span>",

		"saveServerConfig": "サーバー設定の保存",
		"saveServerConfigSuccess": "サーバー設定の保存に成功しました",
		"saveServerConfigPortError": "中央サーバー、アプリケーションサーバー、WEBサーバーのポートはすべて同じであるか、すべて異なる必要があります",

		"saveServerSSLConfig": "SSL設定の保存",
		"saveServerSSLConfigSuccess": "SSL設定の保存に成功しました",

        "sslConfig": "SSL使用の有無",

		"serverTaskConfig": "サーバータスク",

		"proxyCenterEnable": "プロキシ中央サービス",
		"proxyApplicationEnable": "プロキシアプリケーションサービス",
		"proxyTimeOut": "プロキシタイムアウト（秒）",

		"includes": "有効なアプリケーションモジュール",
		"includesInfo": "ここでは、サーバーが実行を許可するアプリケーションモジュールを選択できます。ここでのみ構成されたアプリケーションモジュールのみが起動します。これにより、クラスター環境でサーバーのパフォーマンスを柔軟に割り当てることができます。ただし、この設定を変更する際は注意してください。誤った構成でサービスが異常終了する可能性があります（サーバーを再起動する必要があります）。",
		"includesInfo2": "<b style='color: #666666'>有効にする組み込みアプリケーションを選択：</b> モジュールを選択しない場合、すべてのモジュールが有効になります。",
		"includesInfo3": "<b style='color: #666666'>有効にするカスタムアプリケーション：</b> 下の入力欄にカスタムアプリケーション名を入力し、半角コンマで区切ります。",

		"saveIncludes": "有効なアプリケーションモジュールの構成を保存",
		"saveExcludes": "無効なアプリケーションモジュールの構成を保存",

        "excludes": "無効なアプリケーションモジュール",
		"excludesInfo": "ここでは、サーバーで実行しないアプリケーションモジュールを選択できます。ここで構成されたアプリケーションモジュールは起動されません。これにより、クラスター環境でサーバーのパフォーマンスを柔軟に割り当てることができます。ただし、この設定を変更する際は注意してください。誤った構成でサービスが異常終了する可能性があります（サーバーを再起動する必要があります）",
		"excludesInfo2": "<b style='color: #666666'>無効にする組み込みアプリケーションを選択：</b> モジュールを選択しない場合、すべてのモジュールが無効になります。",
		"excludesInfo3": "<b style='color: #666666'>無効にするカスタムアプリケーション：</b> 下の入力欄にカスタムアプリケーション名を入力し、半角コンマで区切ります。",

		"includesAll": "すべてのモジュールを有効化",
		"includesSelect": "有効化するモジュールを選択",
		"includesModules": "有効化されたモジュール",
		"selectModules": "選択可能なモジュール",

		"excludesNone": "モジュールを無効にしない",
		"excludesSelect": "無効にするモジュールを選択",

		"saveServerIncludesSuccess": "有効なアプリケーションモジュールの構成を保存しました",
		"saveServerExcludesSuccess": "無効なアプリケーションモジュールの構成を保存しました",

        "requestLogEnable": "HTTPログを有効にする",
		"requestLogBodyEnable": "ボディ内容を記録",
		"requestLogRetainDays": "ログの保持日数",
		"requestLogInfo": "ここでサーバーのHTTPログに関連する設定を行います（サーバーを再起動する必要があります）：" +
			"<ul><li>HTTPログを有効にすると、ログファイルはサーバーのlogsディレクトリに保存されます。（三要員管理を有効にしている場合、HTTPログは常に有効です）</li>" +
			"<li>ボディ内容を記録すると、詳細なログ情報が取得できますが、ディスクスペースの使用量とサーバーの負荷が大幅に増加する可能性があります。</li>" +
			"<li>ログを保持できる最大日数を設定すると、その日数を超えるログファイルは削除されます。</li></ul>",
		
		"webSocketEnable": "WebSocketを有効にする",
		"webSocketEnableInfo": "WebSocketは、サーバーからWEBユーザーへのメッセージ通知やチャットなどに使用されます。WebSocketを有効にする場合、nginx、WAFなどのネットワークシステムを正しく構成してWebSocketプロトコル通信を許可する必要があります。（サーバーを再起動する必要があります）",

		"deployWarEnable": "カスタムアプリケーションのフロントエンドデプロイを許可する",
		"deployWarEnableInfo": "この設定は、カスタムアプリケーション（war）がWEBでアップロードおよびデプロイされるかどうかを制御します（サーバーを再起動する必要があります）",

		"deployResourceEnable": "フロントエンドリソースのデプロイを許可する",
		"deployResourceEnableInfo": "この設定は、フロントエンドコンポーネントと静的リソースがWEBでアップロードおよびデプロイされるかどうかを制御します（サーバーを再起動する必要があります）",

        "statEnable": "Druid統計の有効化",
		"statExclusions": "統計の無視パス",
		"statEnableInfo": "データベース接続、SQL実行、HTTPリクエストなどに関するDruid統計情報を有効にします。統計結果ページは以下のURLからアクセスできます：<a href='{url}' target='_blank'>Druidモニター</a>。",
		
		"exposeJest": "Restful APIドキュメントページの出力",
		"exposeJestInfo": "Restful APIドキュメントを出力しますか？ APIドキュメントは以下のURLからアクセスできます：<a href='{url}' target='_blank'>Restful API</a>。",
		
		"scriptingBlockedClasses": "サーバースクリプトで使用が禁止されているJavaクラス",
		"scriptingBlockedClassesInfo": "ここでサーバースクリプトで使用が許可されていないJavaクラスを設定します。カンマで区切って入力します。",
		
		"httpWhiteList": "外部HTTPインターフェースサービスアドレスのホワイトリスト",
		"httpWhiteListInfo": "外部HTTPインターフェースサービスアドレスのホワイトリストです。*は制限なしを意味し、カンマで区切って入力します。",
		
		"refererHeadCheckRegular": "リクエストRefererの検証",
		"refererHeadCheckRegularInfo": "ここで、サーバーがリクエストのRefererヘッダーを検証するルールを設定できます。正規表現を設定して、正規表現に一致するReferer値のリクエストのみ許可されます。合理的な設定を行うことでCSRF攻撃を効果的に防ぐことができます。例：(.+?)o2oa.net(.+?)を設定すると、Refererに「o2oa.net」を含むリクエストのみ許可されます。",

        "contentSecurityPolicy": "Content-Security-Policyレスポンスヘッダ",
		"contentSecurityPolicyInfo": "HTTPレスポンスヘッダのContent-Security-Policyは、サイト管理者が特定のページでどのリソースをロードできるかを制御するのに役立ちます。設定されたポリシーは、主にサーバーのソースとスクリプトエンドポイントを指定するために使用されます。これはクロスサイトスクリプト攻撃（Cross-Site Script）を防ぐのに役立ちます。",
		"contentSecurityPolicyInfo2": "Content-Security-Policyレスポンスヘッダに関する詳細情報は、次を参照してください：<a target='_blank' href='https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Security-Policy'>https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Security-Policy</a>",
		
		"accessControlAllowOrigin": "クロスオリジン許可",
		"accessControlAllowOriginInfo": "クロスオリジンリソース共有許可、HTTPの戻り値Access-Control-Allow-Originヘッダー識別子を設定し、CORS攻撃を防ぐために使用できます。例：https://www.o2oa.net",
		
		"personUnitOrderByAsc": "ユーザーと組織の昇順ソート",
		"personUnitOrderByAscInfo": "ユーザーと組織データを取得する際に、昇順でソートするかどうかを設定します。デフォルトはtrueで、それ以外の場合は降順でソートされます。",
		
		"attachmentConfig": "添付ファイルのアップロード設定",
		"attachmentConfigInfo": "ここでは、システムで許可されている添付ファイルのサイズとタイプを設定できます。",

        "fileSize": "添付ファイルのサイズ制限",
		"fileSizeInfo": "M単位、最大2048M",
		"fileTypeIncludes": "アップロード許可された添付ファイルの種類",
		"fileTypeIncludesInfo": "アップロード許可された添付ファイルの種類を設定し、拡張子をカンマで区切って指定します",
		"fileTypeExcludes": "アップロード禁止の添付ファイルの種類",
		"fileTypeExcludesInfo": "アップロード禁止の添付ファイルの種類を設定し、拡張子をカンマで区切って指定します",
		"dumpData": "データの自動バックアップ",
		"dumpDataInfo": "O2OAは定期的にデータを自動バックアップできます。ここで設定してください。",
		"dumpEnable": "有効化するかどうか",
		"dumpCron": "定期的な式",
		"dumpSize": "最大のバックアップ回数",
		"dumpPath": "バックアップパス",
		"saveDump": "自動バックアップの設定を保存",
		"saveDumpSuccess": "自動バックアップの設定を保存しました",
		"restoreData": "データの自動復元",
		"restoreDataInfo": "O2OAは定期的にデータを自動的に復元できます。ここで設定してください。",
		"restoreEnable": "有効化するかどうか",
		"restoreCron": "定期的な式",
		"restorePath": "復元パス",
		"saveRestore": "自動復元の設定を保存",
		"saveRestoreSuccess": "自動復元の設定を保存しました",
		"reloadServerConfig": "サーバー設定を再読み込み"
    },
    "_worktimeConfig": {
		"amWorktime": "午前の勤務時間",
		"pmWorktime": "午後の勤務時間",
		"holidays": "休日",
		"workdays": "勤務日",
		"weekends": "週末",

		"amWorktimeInfo": "ここで勤務日の午前の勤務時間範囲を設定します。",
		"pmWorktimeInfo": "ここで勤務日の午後の勤務時間範囲を設定します。",
		"holidaysInfo": "休日を設定して、通常の勤務日である日付をここに追加して休日として扱います。",
		"workdaysInfo": "通常の非勤務日である日付を勤務日として扱うために、ここに追加してください。",
		"weekendsInfo": "週末を設定するには、週末の非勤務日として設定したい曜日を下で選択してください。",

		"timeRangeTo": "から",
		"startTime": "開始時間",
		"endTime": "終了時間",

		"weekData": {
			"月曜日": 2,
			"火曜日": 3,
			"水曜日": 4,
			"木曜日": 5,
			"金曜日": 6,
			"土曜日": 7,
			"日曜日": 1
		}
	},
    "_cacheConfig": {
		"type": "キャッシュタイプ",
		"typeInfo": "O2OA システムは guava と redis の 2 つのキャッシュをサポートしており、デフォルトで guava を使用します。",

		"guava_maximumSize": "キャッシュ最大容量",
		"guava_maximumSizeInfo": "キャッシュの最大容量、オブジェクト数。デフォルト値: 3000",
		"guava_expireMinutes": "有効期限",
		"guava_expireMinutesInfo": "有効期限、分単位。デフォルト値: 30",

		"redis": "redis サービス設定",
		"redisInfo": "ここで redis サービスを設定します。",
		"redis_host": "サーバーアドレス",
		"redis_port": "サーバーポート",
		"redis_user": "認証ユーザー",
		"redis_password": "認証パスワード",
		"redis_connectionTimeout": "接続待ちタイムアウト",
		"redis_socketTimeout": "レスポンス待ちタイムアウト",
		"redis_sslEnable": "SSL を有効にする",
		"redis_index": "データベース番号",

		"saveRedis": "redis 設定を保存",
		"saveRedisSuccess": "redis 設定を保存しました",
	},
    "_processConfig": {
        "baseConfig": "基本設定",
		"timerConfig": "タイマー設定",

		"maintenanceIdentity": "プロセスメンテナンス身分",
		"selectMaintenanceIdentity": "プロセスメンテナンス身分の選択",
		"maintenanceIdentityInfo": "プロセスワーク中に予期しないエラーが発生し、該当する処理者が見つからない場合、システムはまず作成者の身分にワークを割り当てようと試み、作成者の身分も取得できない場合、ここで設定された身分に割り当てられます。",

		"formVersionCount": "フォームの履歴バージョンの保持数",
		"formVersionCountInfo": "フォームを保存する際、システムは特定の状況で以前のデザインを取得するために、以前のバージョンのコピーを保持できます。ここではフォームの履歴バージョンを最大で保持できる数が設定され、この数を超えると最も古い履歴バージョンが削除されます。",

		"processVersionCount": "プロセスの履歴バージョンの保持数",
		"processVersionCountInfo": "プロセスを保存する際、システムは特定の状況で以前のデザインを取得するために、以前のバージョンのコピーを保持できます。ここではプロセスの履歴バージョンを最大で保持できる数が設定され、この数を超えると最も古い履歴バージョンが削除されます。",

		"scriptVersionCount": "スクリプトの履歴バージョンの保持数",
		"scriptVersionCountInfo": "スクリプトを保存する際、システムは特定の状況で以前のデザインを取得するために、以前のバージョンのコピーを保持できます。ここではスクリプトの履歴バージョンを最大で保持できる数が設定され、この数を超えると最も古い履歴バージョンが削除されます。",

		"docToWordType": "文書エディタコンポーネントのWORD変換方法",
		"docToWordTypeInfo": "文書エディタコンポーネントが 'Service' としてWORDに変換されるように設定された場合、バックエンドサービスがWORDに変換します。" +
			"O2OAシステムはローカルサービス変換またはクラウドサービス変換をサポートしており、クラウドサービス変換を使用するとWORD形式との互換性が向上しますが、まずO2クラウドに接続する必要があります。O2クラウドに接続するには「クラウドサービス設定」で接続してください。",
		"docWordTypeSelect": {
			"local": "ローカルサービス",
			"cloud": "クラウドサービス"
		},

        "press": "作業リマインダー設定",
		"pressInfo": "プロセス設定で手動アクティビティノードにリマインダーを開始できるように設定でき、作業を処理した人はこの作業の現在の担当者に対して処理リマインダーを開始できます。この動作について、時間帯内の回数制限をここで設定できます。",
		"pressInfo1": "分以内に最大",
		"pressInfo2": "回リマインダーを開始",

		"executorCount": "トランジションエグゼキュータの数",
		"executorCountInfo": "プロセストランジションを処理するエグゼキュータの数です。デフォルトは32で、一般的に変更をお勧めしません。",

		"executorQueueBusyThreshold": "エグゼキュータキューのビジー閾値",
		"executorQueueBusyThresholdInfo": "プロセストランジションエグゼキュータキューのビジー閾値です。デフォルトは5で、一般的に変更をお勧めしません。",

		"timerInfo": "O2OAプロセスプラットフォームは、プロセスタスクを処理するためにいくつかのタイマーが必要です。ここではこれらのタイマーを構成できます。（すべてのタイマーの変更にはサーバーの再起動が必要です。）",

		"enable": "有効化",
		"cron": "スケジュール式",
		"urge": "催促タイマー",
		"urgeInfo": "アクティビティにタイムアウト時間が設定されている場合、このタイマーは指定された時間に到達する見込みのある保留中のタスクをチェックし、そのタスクの処理者に催促メッセージを送信します。",

        "expire": "タイムアウトタイマー",
		"expireInfo": "アクティビティにタイムアウト時間が設定されている場合、このタイマーは待機中のタスクが指定された時間を超えているかどうかを確認し、これらの待機中のタスクをタイムアウトとしてマークします。",

		"touchDelay": "タイムアウトアクティビティトリガータイマー",
		"touchDelayInfo": "このタイマーはプロセス内のタイムアウトアクティビティをトリガーするために使用されます。",

		"deleteDraft": "ドラフト削除タイマー",
		"deleteDraftInfo": "プロセスでは、ドラフトモードを使用してプロセスインスタンスを作成できます。このモードではプロセスを正式に開始する前に保存されません。このタイマーは長期間流れていないドラフトファイルを削除できます。",

		"thresholdMinutes": "時間閾値（分）",
		"thresholdMinutesInfo": "閾値を設定します。分単位で測定され、この時間を超えると削除可能なドラフトと見なされます。デフォルトは10日です。",

		"passExpired": "自動転送タイマー",
		"passExpiredInfo": "プロセスアクティビティでタイムアウト処理を有効にした場合、このタイマーは既にタイムアウトした待機中のタスクを転送しようと試みます。",

		"touchDetained": "滞在タスクチェックタイマー",
		"touchDetainedInfo": "このタイマーは長時間滞在しているタスクを検索し、タスクを進行させようと試みます。これにより、人員の変更などによるタスクの滞在が自動的に処理できます。",
		"thresholdMinutesInfo_touchDetained": "このタイマーはこの閾値を超える滞在時間のタスクを処理し、デフォルトは1440分（1日）です。",

		"updateTable": "データベース同期タイマー",
		"updateTableInfo": "プロセスでプロセスデータをデータベースにマッピングする設定を行った場合、このタイマーはマッピングデータキューを処理するのに使用されます。",

		"archiveHadoop": "Hadoopへのアーカイブ",
		"archiveHadoopInfo": "O2OAは完了したタスクデータをHadoopにアーカイブする機能をサポートしています。ここでHadoop関連の設定を行うことができます。",
		"fsDefaultFS": "Hadoopアドレス",
		"username": "Hadoopユーザー名",
		"path": "パスプレフィックス",
		"saveHadoop": "Hadoop設定を保存",
		"saveHadooping": "保存中...",
		"saveHadoopSuccess": "保存に成功しました",

		"merge": "アーカイブタイマー",
		"mergeInfo": ""
    },
    "_queryConfig": {
		"queryIndexConfig": "インデックス構成",
		"workConfig": "進行中のドキュメント",
		"workCompletedConfig": "完了したドキュメント",
		"documentConfig": "コンテンツ管理ドキュメント",
		"indexTools": "インデックスツール",

		"work": "進行中",
		"workCompleted": "完了",
		"document": "コンテンツ管理",

		"touchWorkIndex": "進行中のドキュメントの全文検索を実行",
		"touchWorkIndexInfo": "インデックスを初めて有効にするか、古いバージョンからアップグレードする場合、システムがアイドル状態のときに進行中のドキュメントの全文検索を即座にトリガーできます。",
		"touchWorkIndexAction": "進行中のドキュメントの全文検索を即座に実行",

        "touchWorkCompletedIndex": "完了したドキュメントの全文検索を実行",
		"touchWorkCompletedIndexInfo": "インデックスを初めて有効にするか、古いバージョンからアップグレードする場合、システムがアイドル状態のときに完了したドキュメントの全文検索を即座にトリガーできます。",
		"touchWorkCompletedIndexAction": "完了したドキュメントの全文検索を即座に実行",

		"touchDocumentIndex": "コンテンツ管理ドキュメントの全文検索を実行",
		"touchDocumentIndexInfo": "インデックスを初めて有効にするか、古いバージョンからアップグレードする場合、システムがアイドル状態のときにコンテンツ管理ドキュメントの全文検索を即座にトリガーできます。",
		"touchDocumentIndexAction": "コンテンツ管理ドキュメントの全文検索を即座に実行",

		"optimizeIndex": "インデックスの最適化を実行",
		"optimizeIndexInfo": "インデックスの最適化は、インデックスのストレージスペースを圧縮し、検索パフォーマンスを向上させるためにインデックス構造を最適化します。インデックスの最適化を実行するには時間がかかることがあり、システムがアイドル状態のときに即座にトリガーできます。",
		"optimizeIndexAction": "インデックスの最適化を即座に実行",

        "indexActionConfirmTitle": "{type}全文検索実行確認",
		"indexActionConfirm": "全文検索は多くのサーバーリソースを使用し、サーバーの応答が遅くなる可能性があります。システムがアイドル状態のときに実行することをお勧めします。<br><br>{type}ドキュメントの全文検索を実行しますか？",
		"indexActionSuccess": "{type}全文検索タスクがキューに追加されました。システムが即座に実行されます！",

		"optimizeIndexConfirmTitle": "インデックス最適化実行確認",
		"optimizeIndexConfirm": "インデックス最適化実行は多くのサーバーリソースを使用し、サーバーの応答が遅くなる可能性があります。システムがアイドル状態のときに実行することをお勧めします。<br><br>インデックス最適化を実行しますか？",
		"optimizeIndexSuccess": "インデックス最適化タスクがキューに追加されました。システムが即座に実行されます！",

		"restartServerInfo": "<span style='color: red'>インデックス構成の変更はサーバーを再起動した後に適用されます！</span>",

		"enable": "インデックスサービスを有効にするかどうか",

        "modeConfig": "インデックス保存場所",
		"modeConfigInfo": "インデックス保存場所を選択します。デフォルトは「ローカルファイルシステム」です。",
		"indexMode": "インデックス保存場所",
		"modeOptions": {
			"localDirectory": "ローカルファイルシステム",
			"hdfsDirectory": "Hadoopファイルシステム",
			"sharedDirectory": "共有ファイルシステム"
		},
		"hdfsDirectoryDefaultFS": "Hadoopファイルシステムアドレス",
		"hdfsDirectoryPath": "Hadoopファイルシステムディレクトリ",
		"sharedDirectoryPath": "共有ファイルシステムディレクトリ",

		"optimizeIndexEnable": "インデックスの最適化",
		"optimizeIndexEnableInfo": "インデックスの最適化を有効にすると、インデックスの保存領域を圧縮し、インデックス構造を最適化して検索性能を向上させることができます。",
		"optimizeIndexCron": "インデックスの最適化スケジュール設定",
		"isEnable": "有効化",
		"cron": "スケジュール式",

        "dataStringThreshold": "ビジネスデータの最大テキスト長さ閾値",
		"dataStringThresholdInfo": "ビジネスデータの最大テキスト長さ閾値。この閾値を超えるデータはインデックスに記録されません。",

		"summaryLength": "概要の長さ",

		"attachmentMaxSize": "添付ファイルインデックス閾値",
		"attachmentMaxSizeInfo": "添付ファイルインデックス閾値（メガバイト）。この値を超える添付ファイルはインデックスされません。",

		"cleanupThresholdDays": "検索コンテンツのクリーンアップ閾値",
		"cleanupThresholdDaysInfo": "検索コンテンツのクリーンアップ閾値（日）。この期間更新されていないインデックスは削除されます。",

		"searchMaxPageSize": "検索の最大ページサイズ",
		"searchMaxPageSizeInfo": "検索結果の最大ページごとのアイテム数",

		"moreLikeThisMaxSize": "関連推奨の最大返却数",
		"moreLikeThisMaxSizeInfo": "関連推奨検索の最大返却数",

		"workIndexAttachment": "流れ中の文書の添付ファイルをインデックス化するかどうか",
		"workIndexAttachmentInfo": "流れ中の文書の添付ファイルをインデックス化するかどうか。（添付ファイルのインデックス化には、異なるビジネスボリュームに応じて、強力なサーバーパフォーマンスとより多くのメモリが必要な場合があります）",

        "lowFreqWorkEnable": "全文検索を有効にするかどうか",
		"lowFreqWorkEnableInfo": "全文検索は、文書フロー内のすべてのインデックスを更新して、権限とデータの正確性を確保します。",
		"lowFreqWorkCron": "全文検索スケジュール式",
		"lowFreqWorkCronInfo": "全文検索はサーバーリソースを多く使用するため、全文検索を使用する場合、システムがアイドル状態のときにのみ実行することをお勧めします。流れ中のデータ、完了したデータ、コンテンツ管理データの全文検索については、異なる時間帯で実行するように分けることをお勧めします。",
		"lowFreqWorkMaxCount": "一度に実行する全文検索の最大ドキュメント数",
		"lowFreqWorkMaxCountInfo": "一度のインデックス処理に実行される最大ドキュメント数を設定します。この数に達すると、インデックスが停止し、次回のインデックス実行時に前回のドキュメント処理後から続行されます。最大数量と処理時間のどちらかが満たされれば、インデックスは停止します。",
		"lowFreqWorkMaxMinutes": "一度に実行する全文検索の処理最大時間（分）",
		"lowFreqWorkMaxMinutesInfo": "一度のインデックス処理に実行される最大時間を設定します。この時間に達すると、インデックスが停止し、次回のインデックス実行時に前回のドキュメント処理後から続行されます。最大数量と処理時間のどちらかが満たされれば、インデックスは停止します。",
		
		"highFreqWorkEnable": "増分検索を有効にするかどうか",
		"highFreqWorkEnableInfo": "増分検索を有効にすると、文書データやステータスが変更された場合、信号が発信され、指定された時間に増分検索タイマーが実行され、増分信号を取得してドキュメントインデックスを更新します。",
		"highFreqWorkCron": "増分検索タイマー",
		"highFreqWorkCronInfo": "増分検索を定期的に実行するためのスケジュール式",
		"highFreqWorkMaxCount": "一度に実行する増分検索の最大処理量",
		"highFreqWorkMaxMinutes": "一度に実行する増分検索の処理最大時間（分）",


        "workCompletedIndexAttachment": "完了文書の添付ファイルをインデックス化するかどうか",
		"workCompletedIndexAttachmentInfo": "完了文書の添付ファイルをインデックス化するかどうか。（添付ファイルをインデックス化するには、異なるビジネスボリュームに応じて、より強力なサーバーパフォーマンスと大容量のメモリが必要かもしれません。）",
		
		"lowFreqWorkCompletedEnable": "全文検索を有効にするかどうか",
		"lowFreqWorkCompletedEnableInfo": "全文検索は完了文書のすべてのインデックスを更新して、権限とデータの正確性を保証します。",
		"lowFreqWorkCompletedCron": "全文検索スケジュール式",
		"lowFreqWorkCompletedCronInfo": "全文検索はサーバーリソースを多く使用するため、全文検索を使用する場合、システムがアイドル状態のときにのみ実行することをお勧めします。流れ中のデータ、完了したデータ、コンテンツ管理データの全文検索については、異なる時間帯で実行するように分けることをお勧めします。",
		"lowFreqWorkCompletedMaxCount": "一度に実行する全文検索の最大ドキュメント数",
		"lowFreqWorkCompletedMaxCountInfo": "一度のインデックス処理に実行される最大ドキュメント数を設定します。この数に達すると、インデックスが停止し、次回のインデックス実行時に前回のドキュメント処理後から続行されます。最大数量と処理時長のどちらかが満たされれば、インデックスは停止します。",
		"lowFreqWorkCompletedMaxMinutes": "一度に実行する全文検索の処理最大時間(分)",
		"lowFreqWorkCompletedMaxMinutesInfo": "一度のインデックス処理に実行される最大時間を設定します。この時間に達すると、インデックスが停止し、次回のインデックス実行時に前回のドキュメント処理後から続行されます。最大数量と処理時長のどちらかが満たされれば、インデックスは停止します。",


        "highFreqWorkCompletedEnable": "増分索引を有効にするかどうか",
		"highFreqWorkCompletedEnableInfo": "増分索引を有効にすると、文書データや状態が変更されると信号が発生し、指定された時間に増分索引スケジューラが実行され、増分信号を取得して文書インデックスを更新します。",
		"highFreqWorkCompletedCron": "増分索引スケジューラ",
		"highFreqWorkCompletedCronInfo": "増分索引は定期的に実行されるスケジュール式です。",
		"highFreqWorkCompletedMaxCount": "増分索引の単一処理の最大数",
		"highFreqWorkCompletedMaxMinutes": "増分索引の単一処理の最大時間（分）",

		"documentIndexAttachment": "コンテンツ管理文書の添付ファイルをインデックス化するかどうか",
		"documentIndexAttachmentInfo": "コンテンツ管理文書の添付ファイルをインデックス化するかどうかです。 （添付ファイルをインデックス化するには、異なるビジネスボリュームに応じて、より強力なサーバーパフォーマンスとより大きなメモリが必要かもしれません。）",

        "lowFreqDocumentEnable": "全文検索を有効にする",
		"lowFreqDocumentEnableInfo": "全文検索を有効にすると、“情報”タイプのすべてのコンテンツ管理ドキュメントのインデックスが更新され、権限とデータの正確性が保たれます。",
		"lowFreqDocumentCron": "全文検索スケジューラ",
		"lowFreqDocumentCronInfo": "全文検索は多くのサーバーリソースを使用するため、全文検索を有効にする場合は、システムがアイドル状態のときにのみ実行するように設定することをお勧めします。留意すべきは、ドキュメントタイプに応じて、全文検索、完了データ、コンテンツ管理データの全文検索を異なる時間帯に実行することです。",
		"lowFreqDocumentMaxCount": "単一の実行での最大文書数",
		"lowFreqDocumentMaxCountInfo": "一度のインデックス処理で処理できる最大ドキュメント数を設定し、この数に達するとインデックスが停止し、次回の実行時に前回処理したドキュメントから継続します。最大数と処理時間のいずれかが満たされると、インデックスが停止します。",
		"lowFreqDocumentMaxMinutes": "全文検索の実行処理時間（分）",
		"lowFreqDocumentMaxMinutesInfo": "一度のインデックス処理で許容される最大時間を設定し、この時間に達するとインデックスが停止し、次回の実行時に前回処理したドキュメントから継続します。最大数と処理時間のいずれかが満たされると、インデックスが停止します。",

		"highFreqDocumentEnable": "増分検索を有効にする",
		"highFreqDocumentEnableInfo": "増分検索を有効にすると、ドキュメントデータまたはステータスが変更された場合、シグナルを送信し、指定された時間に増分検索スケジューラが実行され、増分シグナルを取得しドキュメントのインデックスを更新します。",
		"highFreqDocumentCron": "増分検索スケジューラ",
		"highFreqDocumentCronInfo": "増分検索は定期的に実行される式です。",
		"highFreqDocumentMaxCount": "単一の処理での最大数",
		"highFreqDocumentMaxMinutes": "単一の処理での最大時間（分）"

    },
    "_appConfig": {
        "connectConfig": "接続設定",
		"moduleConfig": "モジュール設定",
		"iconConfig": "アイコン設定",

		"cloudConnect": "クラウドサービス接続確認",
		"connectedInfo": "<span style='color:#5fbf78'>[O2クラウドサービスに接続済み]</span>",
		"notConnectedInfo": "<span style='color:red'>[O2クラウドサービスに接続されていません]</span> クラウドサービス構成ページで登録およびログインしてください。",

		"httpProtocol": "Webアクセスプロトコル",
		"httpProtocolInfo": "モバイルデバイスからセンターサービスにアクセスする際に使用するHTTPプロトコルまたはHTTPSプロトコルを選択してください。",

		"centerServer": "センターサーバー",
		"centerServerInfo": "外部サービスのためのセンターサーバーのIPアドレスまたはドメインとポートです。",

		"webServer": "Webサーバー",
		"webServerInfo": "外部サービスのためのWebサーバーのIPアドレスまたはドメインとポートです。ドメインまたはIPアドレスが空白または「127.0.0.1」の場合、センターサーバーのアドレスが使用されます。",

		"applicationServer": "アプリケーションサーバー",
		"applicationServerInfo": "外部サービスのためのアプリケーションサーバーのIPアドレスまたはドメインとポートです。ドメインまたはIPアドレスが空白または「127.0.0.1」の場合、センターサーバーのアドレスが使用されます。",

		"editServer": "サーバーアドレスの編集",
		"host": "ドメインまたはIPアドレス",
		"port": "ポート",

        "connectTest": "モバイル接続テスト",
		"connectTestInfo": "QRコードをスキャンして、外部ネットワークからサーバーに接続できるかどうかを確認します。",
		"getQrcode": "接続テストQRコードの生成",

		"mobileIndex": "モバイルホームページ設定",
		"mobileIndexInfo": "モバイルアプリのホームページをデフォルトのアプリスタイルまたは特定のポータルページに設定できます。",

		"simpleMode": "モバイルシンプルモード",
		"simpleModeInfo": "モバイルでシンプルモードを有効にすると、ホームページと設定ページのみ表示されます。",

		"appIndexPage": "モバイルアプリページ設定",
		"appIndexPageInfo": "モバイルアプリのいくつかの主要なページを表示するかどうかを設定できます。",
		"appIndexPageHome": "ホーム",
		"appIndexPageIM": "メッセージ",
		"appIndexPageContact": "連絡先",
		"appIndexPageApp": "アプリ",
		"appIndexPageSettings": "設定",

		"appIndexCenteredTitle": "モバイルアプリのホームページを中央揃えにするかどうか",
		"appIndexCenteredInfo": "モバイルアプリのホームページを中央揃えにすると、ページ数は設定できなくなります。",

        "appIndexCmsFilterTitle": "ホームページ情報センター",
		"appIndexCmsFilterCategoryInfo": "情報センターリストのカテゴリクエリ条件です。空白の場合、すべてのクエリが実行されます。",
		"appIndexTaskFilterTitle": "ホームページオフィスセンター",
		"appIndexTaskFilterProcessInfo": "オフィスセンターリストのプロセスクエリ条件です。空白の場合、すべてのクエリが実行されます。",
		"appIndexTaskFilterProcessSelectorTitle": "プロセスの選択",
		"appIndexCmsFilterCategroySelectorTitle": "カテゴリの選択",

		"systemMessageSwitch": "システム通知の表示",
		"systemMessageSwitchInfo": "モバイルアプリのメッセージリストにシステム通知を表示するかどうか",
		"systemMessageCanClickInfo": "モバイルアプリのシステム通知をクリックして開くことができるかどうか",

		"contactPermissionView": "モバイルアプリの連絡先権限ビュー",
		"contactPermissionViewInfo": "アプリストアに「連絡先」アプリをインストールする必要があります。このアプリには連絡先の権限設定ビューが含まれています。",

		"appExitAlert": "アプリ終了警告",
		"appExitAlertInfo": "アプリを終了する際にポップアップウィンドウに表示するメッセージです。空白の場合、ポップアップは表示されません。",

		"nativeAppList": "アプリリスト",
		"nativeAppListInfo": "モバイルアプリで使用するアプリを設定し、無効にするアプリを指定できます。",

        "imageNames": {
			"application_top": {"text": "アプリケーションのトップページ画像", "action": "ApplicationTop"},
			"index_bottom_menu_logo_blur": {"text": "ホームページの下部メニューアイコン（選択されていない）", "action": "MenuLogoBlur"},
			"index_bottom_menu_logo_focus": {"text": "ホームページの下部メニューアイコン（選択済み）", "action": "MenuLogoFocus"},
			"launch_logo": {"text": "起動ロゴ画像", "action": "LaunchLogo"},
			"login_avatar": {"text": "ログイン画面のデフォルトプロフィール画像", "action": "LoginAvatar"},
			"process_default": {"text": "プロセスのデフォルトアイコン", "action": "ProcessDefault"},
			"setup_about_logo": {"text": "情報ページのアイコン", "action": "SetupAboutLogo"}
		},
		"imageSzie": "サイズ",
		"changeImage": "画像の変更",
		"defaultImage": "デフォルト画像",
		"defaultImageTitle": "デフォルト画像の確認",
		"defaultImageInfo": "{name}をデフォルト画像に変更しますか？",
    },
    "_integrationConfig": {
        "title": "モバイルアプリの統合",
		"dingding": "ディンディング統合",
		"mPweixin": "WeChat公式アカウントの統合",
		"qiyeweixin": "企業WeChatの統合",
		"weLink": "華為WeLinkの統合",
		"zhengwuDingding": "浙政ディンディング統合",


        "enable": "ディンディング統合を有効にするかどうか",
		"corpId": "ディンディングCorpId",
		"agentId": "ディンディングAgentId",
		"appKey": "アプリケーションの一意の識別子",
		"appSecret": "アプリケーションのシークレットキー",
		"syncCron": "同期チェックコールバックシグナルスケジュール",
		"forceSyncCron": "強制同期スケジュール",
		"oapiAddress": "ディンディングAPIサーバーアドレス",
		"token": "コールバックトークン",
		"encodingAesKey": "コールバックエンコードAesキー",
		"workUrl": "ディンディングメッセージを開くURL",
		"messageRedirectPortal": "処理完了後にポータルにリダイレクト",
		"messageEnable": "メッセージプッシュを有効にするかどうか",
		"scanLoginEnable": "ディンディングQRコードログインを有効にするかどうか",
		"scanLoginAppId": "ディンディングQRコードログインのAppId",
		"scanLoginAppSecret": "ディンディングQRコードログインのAppSecret",
		"attendanceSyncEnable": "出勤情報を有効にするかどうか",

        "enableInfo": "O2OAプラットフォームは、ネイティブのAndroidおよびiOSモバイルアプリを提供し、マイクロアプリの形式でAlibaba DingTalkに統合できます。これにより、DingTalkの企業連絡先をローカルの組織人員構造として同期し、タスクなどの通知を直接DingTalkにプッシュしてメッセージを通知できます。（サーバーの再起動が必要です）",
		"enableInfo2": "<span class='mainColor_color'>O2OAがDingTalkに正常に接続されると、O2OAは自動的にDingTalkからすべての人員と組織を取得して同期します。 O2OAのすべての人員と組織は、企業DingTalkで作成された組織構造に従います（ローカルに作成された人員と組織は削除されず、人員と組織の重複が発生する可能性があります）。</span>",
		"enableInfo3": "O2OAとDingTalkの統合の詳細については、こちらをご覧ください：<a href='https://www.o2oa.net/search.html?q=%E9%92%89%E9%92%89' target='_blank'>DingTalk</a>",

		"syncCronInfo": "コールバック信号が同期チェックをトリガし、デフォルトで10分ごとに実行されます。その間にDingTalkコールバック信号が受信される場合、同期タスクが人員の同期を実行します（DingTalkでコールバック設定が必要です）。",
		"forceSyncCronInfo": "強制同期のタイミング設定、デフォルトでは毎日8時と12時に人員と組織の強制同期が行われます。",
		"oapiAddressInfo": "DingTalk APIサーバーアドレス、通常は変更する必要はありません。",
		"workUrlInfo": "DingTalkメッセージを開く作業のURLアドレス、例：https://sample.o2oa.net/x_desktop/",
		"messageRedirectPortalInfo": "DingTalkメッセージ処理が完了した後、特定のポータルページにリダイレクトできます。",

		"saveDingding": "DingTalk設定を保存",
		"saveDingdingSuccess": "DingTalk設定の保存に成功しました",

        "mpweixinText": {
            "enable": "有効化",
			"enablePublish": "メニューの公開を有効化",
			"appid": "WeChat Appid",
			"appSecret": "WeChat AppSecret",
			"token": "WeChat Token",
			"encodingAesKey": "WeChat encodingAesKey",
			"portalId": "処理完了後に指定のポータルに移動",
			"workUrl": "WeChat公式アカウントメッセージの作業を開くURL",
			"scriptId": "サービススクリプトを実行",
			"messageEnable": "テンプレートメッセージを有効化",
			"tempMessageId": "公式アカウントのテンプレートメッセージID",
			"fieldList": "テンプレートフィールドの設定",
			"tempName": "テンプレートフィールド",
			"name": "ビジネスフィールド",

			"workUrlInfo": "WeChat公式アカウントメッセージの作業を開くURLアドレス、例：https://sample.o2oa.net/x_desktop/",
			"enableInfo": "O2OAはWeChat公式アカウントの統合をサポートし、ユーザーはWeChat公式アカウントをフォローして業務処理を行うことができます。また、タスクの通知をサポートします（サーバーの再起動が必要です）。",
			"enableInfo2": "WeChat公式アカウントに関連する詳細はこちらをご覧ください：<a href='https://www.o2oa.net/search.html?q=%E5%BE%AE%E4%BF%A1%E5%85%AC%E4%BC%97%E5%8F%B7' target='_blank'>WeChat公式アカウント</a>",
			"enablePublishInfo": "メニューの公開を有効にすると、O2OAで構成したメニュー機能をWeChat公式アカウントに公開できます。WeChat公式アカウントメニューを構成するには、APPツール - WeChat公式アカウントメニュー設定で設定できます。",
			"portalIdInfo": "メッセージ処理が完了した後、特定のポータルページに移動できます。",
			"scriptIdInfo": "公式アカウントからテキストメッセージを受信した場合、ここでプラットフォームサービス管理のインタフェースを実行するように指定できます。",
			"fieldListInfo": "これはテンプレートのコンテンツ内のビジネスフィールドに対応しています。現在、O2OAは次のビジネスフィールドを提供しています：【creatorPerson：作成者、activityName：現在のノード、processName：プロセス名、startTime：開始時間、title：タイトル】",

			"saveMpweixin": "WeChat公式アカウントの設定を保存",
			"saveMpweixinSuccess": "WeChat公式アカウントの設定保存に成功しました"
        },
        "qywenxinText": {
            "enable": "有効化",
			"corpId": "企業WeChat CorpId",
			"agentId": "企業WeChat AgentId",
			"corpSecret": "企業WeChat CorpSecret",
			"syncCron": "同期チェックコールバック信号タイム設定",
			"forceSyncCron": "強制同期タイム設定",
			"apiAddress": "APIサービスアドレス",
			"qrConnectAddress": "QRコードログインサービスアドレス",
			"oauth2Address": "oAuth2サービスアドレス",
			"syncSecret": "アドレス帳同期シークレット",
			"token": "コールバックトークン",
			"encodingAesKey": "コールバックエンコーディングAesキー",
			"workUrl": "メッセージを開く作業のURL",
			"messageRedirectPortal": "処理完了後にポータルにリダイレクト",
			"messageEnable": "メッセージプッシュの有効化",
			"scanLoginEnable": "QRコードログインの有効化",
			"attendanceSyncEnable": "勤怠情報の有効化",
			"attendanceSyncAgentId": "勤怠打刻アプリID",
			"attendanceSyncSecret": "勤怠打刻アプリシークレット",
			"bindEnable": "ユーザーバインディングの有効化",
			"bindEnableInfo": "通常は有効化しないでください。これはユーザー個別のバインディングに使用され、ユーザーおよび組織の同期と競合します！",

            "getUserPrivateInfoMessageTitle": "企業WeChat個人情報取得メッセージ送信",
			"getUserPrivateInfoMessageDesc": "企業WeChatの新しいバージョンの同期APIは、ユーザーの個人情報（電話番号、メールアドレスなど）の取得を制限しており、現在の同期プログラムではユーザー名とユーザーIDのみを取得できます。 以下のメッセージ送信機能は、ユーザーに個人情報を取得する権限を付与するメッセージを送信するものです。 ユーザーがこのメッセージをクリックすると、このプログラムは必要なユーザー情報を読み取ることができます！",
			"getUserPrivateInfoMessageConsumerList": "メッセージ受信者",
			"getUserPrivateInfoMessageFormTitle": "メッセージタイトル",
			"getUserPrivateInfoMessageFormContent": "メッセージ内容",
			"getUserPrivateInfoMessageFormTitleDefault": "【個人情報取得承認】",
			"getUserPrivateInfoMessageFormContentDefault": "アプリはあなたの個人情報を取得する必要があります。 承認をクリックしてください！",
			"getUserPrivateInfoMessageConsumerEmpty": "まずメッセージ受信者を選択してください！",
			"getUserPrivateInfoMessageFormTitleEmpty": "メッセージタイトルは空にできません！",
			"getUserPrivateInfoMessageFormContentEmpty": "メッセージ内容は空にできません！",
			"getUserPrivateInfoMessageConfirmTitle": "注意",
			"getUserPrivateInfoMessageConfirmText": "すべての選択したユーザーと組織の下の人に個人情報を取得するための企業WeChatメッセージを送信してもよろしいですか？",
			"getUserPrivateInfoMessageSendBtn": "メッセージを送信",
			"getUserPrivateInfoMessageSendSuccess": "メッセージの送信が成功しました。企業WeChatで後で確認してください！",


            "syncCronInfo": "コールバックシグナルによる同期チェックをトリガーし、デフォルトでは10分ごとに実行されます。その間に企業WeChatコールバックシグナルが受信された場合、同期タスクが実行されます。（企業WeChatでコールバック設定が必要です）",
			"forceSyncCronInfo": "強制同期のタイミング設定で、デフォルトでは毎日8時と12時に人員と組織の強制同期が行われます。",
			"apiAddressInfo": "企業WeChat APIサーバーアドレスで、通常は変更する必要はありません。",
			"workUrlInfo": "企業WeChatメッセージを開くための作業URLアドレスです。例：https://sample.o2oa.net/x_desktop/",
			"messageRedirectPortalInfo": "企業WeChatメッセージの処理が完了した後、特定のポータルページにリダイレクトできます。",
			"enableInfo": "O2OAはカスタムアプリケーションを使用して企業WeChatに統合することをサポートし、企業WeChatの企業連絡先をローカルの組織人員構造として同期し、タスクなどの通知を直接企業WeChatにプッシュしてメッセージ通知を受け取ることができます。",
			"enableInfo2": "詳細なO2OAと企業WeChatのコンテンツについては、こちらをご覧ください：<a href='https://www.o2oa.net/search.html?q=%E4%BC%81%E4%B8%9A%E5%BE%AE%E4%BF%A1' target='_blank'>企業WeChat</a>",
			"saveText": "企業WeChat構成の保存",
			"saveSuccess": "企業WeChat構成の保存に成功しました"
        },
        "welinkText": {

            "enable": "###の有効化",
			"clientId": "アプリのClientId",
			"clientSecret": "アプリのClientSecret###",
			"syncCron": "同期チェックコールバックシグナルのタイミング",
			"forceSyncCron": "強制同期のタイミング",
			"oapiAddress": "APIサービスアドレス",
			"messageEnable": "メッセージプッシュの有効化",
			"workUrl": "メッセージの作業URLを開く",
			"messageRedirectPortal": "処理が完了した後、ポータルにリダイレクト",

			"enableInfo": "O2OAは、WeLinkの企業内軽量アプリケーションとして統合し、WeLinkの連絡先をローカルの組織人員構造として同期し、タスクなどの通知を直接WeLinkにプッシュしてメッセージ通知を受け取る方法をサポートしています（サーバーの再起動が必要です）。",
			"enableInfo2": "詳細なO2OAとWeLinkの情報については、こちらをご覧ください：<a href='https://www.o2oa.net/search.html?q=welink' target='_blank'>WeLink</a>",

			"syncCronInfo": "コールバックシグナルが同期チェックをトリガーし、デフォルトでは10分ごとに実行されます。その間にWeLinkのコールバックシグナルが受信された場合、同期タスクがトリガーされます（WeLinkでのコールバック設定が必要です）。",
			"forceSyncCronInfo": "強制同期のタイミング設定で、デフォルトでは毎日8時と12時に人員と組織を強制的に同期します。",

			"workUrlInfo": "WeLinkメッセージを開くための作業URLアドレスです。例：https://sample.o2oa.net/x_desktop/",
			"messageRedirectPortalInfo": "WeLinkメッセージの処理が完了した後、特定のポータルページにリダイレクトできます。",

			"saveText": "WeLink構成の保存",
			"saveSuccess": "WeLink構成の保存に成功しました"
        }
    },
    "_storageServer": {
        "innerStorage": "内蔵ストレージサービス",
		"externalStorage": "拡張ストレージサービス",

		"info": "<span style='color: red'>ストレージ設定を変更すると、ほとんどの場合、既存のシステムファイルストレージに影響を及ぼす可能性があるため、慎重に設定を変更してください！</span>",
		"info2": "ストレージ設定を変更する前に、システムデータをバックアップするためにO2OAのバックアップ機能（ctl -dd）を使用することをお勧めします。ストレージ設定を変更した後、サーバーを再起動し、バックアップデータを復元する必要があります（ctl -rd）。すべてのデータベース関連の設定変更にはサーバーの再起動が必要です",

		"saveStorageConfig": "すべてのストレージ設定を保存",
		"saveStorageConfigInfo": "このページの設定は変更後、すぐに保存されず、変更した設定を保存するにはこのボタンをクリックする必要があります",
		"saveStorageConfirm": "ストレージ設定を保存しようとしています<br><span style='color:red'>これは既存のシステムファイルストレージに影響を及ぼす可能性があります。</span><br><br>ストレージ設定を保存してもよろしいですか？",

		"reloadStorageConfig": "すべてのストレージ設定をリロード",
		"reloadStorageConfigInfo": "保存されていない変更を破棄する場合は、このボタンをクリックして設定を再読み込むことができます",
		"reloadStorageConfirm": "この操作はストレージ設定を再読み込みし、保存されていない変更は失われます。ストレージ設定を復元してもよろしいですか？",

		"storageType": "ストレージサービスの種類",
		"storageTypeInfo": "O2OAシステムにはファイルストレージサービスが組み込まれており、必要に応じて外部の拡張ストレージノードを使用できます。",
		"storageTypeData": [
			{"value": 'inner', "label": "内蔵", "text": "内蔵ストレージサービス"},
			{"value": 'external', "label": "外部", "text": "拡張ストレージサービス"}
		],

		"innerInnerInfo": "<span class='mainColor_color'>内蔵ファイルストレージサービスを使用しています</span>、<span style='color:red'>各ストレージノードに異なる名前を設定する必要があります</span>",
		"innerExternalInfo": "<span class='mainColor_color'>拡張ファイルストレージサービスが有効になっています</span>が、内蔵ファイルストレージサービスの設定を変更することはできます。 <span style='color:red'>各ストレージノードに異なる名前を設定する必要があります</span>",

		"innerStorageConfig": "内蔵ストレージサービスの設定",

        "enable": "有効化",
		"port": "ポート",
		"name": "名前",
		"prefix": "プレフィックスパス",
		"deepPath": "深いパスの使用",
		"saveStorage": "保存ストレージ構成",
		"saveStorageSuccess": "ストレージ構成の保存に成功しました",

		"externalInnerInfo": "<span class='mainColor_color'>内蔵ファイルストレージサービスを使用しています</span>が、拡張ファイルストレージサービスの構成を変更できます",
		"externalExternalInfo": "<span class='mainColor_color'>拡張ファイルストレージサービスを有効にしました</span>",

		"enableExternal": "拡張ファイルストレージを有効化",
		"disableExternal": "拡張ファイルストレージを無効化",
		"enableExternalInfo": "拡張ファイルストレージを有効にする場合は、拡張ファイルストレージの構成が完了していることを確認してください。そうでないと、サーバーの正常な動作に支障が生じる可能性があります。拡張ストレージサービスの有効化または無効化は、システムの既存のファイルストレージに影響を与えます。システムデータのバックアップを強くお勧めします。",

		"enableExternalTitle": "拡張ファイルストレージの有効化確認",
		"enableExternalConfirm": "拡張ファイルストレージを有効にし、同時に内蔵ファイルストレージサービスを無効にしようとしています。<br><span style='color:red'>これは既存のシステムファイルに影響を与える可能性があります。</span><br><br>拡張ファイルストレージを有効にしますか？",
		"disableExternalTitle": "拡張ファイルストレージの無効化確認",
		"disableExternalConfirm": "拡張ファイルストレージを無効にし、同時に内蔵ファイルストレージサービスを有効にしようとしています。<br><span style='color:red'>これは既存のシステムファイルに影響を与える可能性があります。</span><br><br>拡張ファイルストレージを無効にしますか？",

		"externalStorageNode": "拡張ストレージノードの設定",
		"addStorageNode": "ストレージノードを追加",
		"editStorageNode": "ストレージノードを編集",
		"inputStorageNodeKey": "ストレージノード識別子を入力してください",
		"inputStorageNodeName": "ストレージノード名を入力してください",

        "external": {
			"protocol": "プロトコル",
			"username": "ユーザー名",
			"password": "パスワード",
			"host": "ホスト",
			"port": "ポート",
			"name": "名前",
			"key": "ノード識別子",
			"protocolData": {
				"webdav": "WebDAV",
				"sftp": "SFTP",
				"ftps": "FTPS",
				"ftp": "FTP",
				"file": "ファイル",
				"hdfs": "HDFS",
				"cifs": "CIFS",
				"ali": "アリババクラウドストレージ",
				"s3": "Amazonクラウドストレージ",
				"min": "MinIOストレージ"
			},
			"protocolDataInfo": {
				"ali": "アプリケーションマーケットにAlibaba Cloud OSS統合プラグインがインストールされていない場合は、まずインストールしてください。",
				"min": "アプリケーションマーケットにMinIOクラウドストレージ統合プラグインがインストールされていない場合は、まずインストールしてください。"
			}
		},
        "removeNodeConfigTitle": "ストレージノード削除確認",
		"removeNodeConfig": "ストレージノード「{name}」を削除しようとしています。この操作はすでにシステムに保存されているファイルに影響を与える可能性があります。<br>「{name}」ストレージノードを削除しますか？",

		"assignNode": "ストレージノード割り当て",
		"assignNodeInfo": "O2OAにはさまざまなタイプのファイルがあり、これらのファイルにストレージノードを割り当てることができます。1つのタイプのファイルには複数のノードを割り当てることができます。",
		"files": {
			"file": "ファイル (file)",
			"processPlatform": "プロセスプラットフォームファイル (processPlatform)",
			"mind": "マインドマップファイル (mind)",
			"meeting": "会議管理ファイル (meeting)",
			"calendar": "スケジュール管理ファイル (calendar)",
			"cms": "コンテンツ管理ファイル (cms)",
			"bbs": "フォーラムファイル (bbs)",
			"teamwork": "タスク管理ファイル (strategyDeploy)",
			"structure": "アプリケーション管理 (structure)",
			"im": "チャットファイル (im)",
			"general": "その他汎用ファイル (general)",
			"custom": "カスタムアプリケーションファイル (custom)"
		},

		"store": "ストレージノード",

		"noStoreNode": "割り当てられていないストレージノード",
		"addStore": "ストレージノードを追加",
		"saveStore": "保存"

    },
    "_appTools": {
        "onlineBuild": "アプリオンラインビルド",
		"mpweixinMenu": "公式アカウントメニュー設定",

		"onlineBuildInfo": " <ul style='padding: 0'><li>現在のモバイルアプリのオンラインビルド機能はAndroidプラットフォームのみサポートしています。</li>" +
			"<li>オンラインビルドを行うには、まず[クラウドサービスの設定]で登録し、ログインする必要があります。</li>" +
			"<li>情報を提出した後、現在のビルドステータスが表示され、ビルドプロセスには時間がかかることがあるため、このページを一時的に離れてビルドが完了するのを待つことができます。その後、APKファイルをこのページからダウンロードできます。</li></ul>",

		"onlineBuildInfo1": "<span class='mainColor_color'>アプリオンラインビルド</span>はアプリストアでより優れたオプションを提供しています。アプリストアで確認して入手できます。",

        "appPack": {
            "formSubmitBtnTitle": "提出してビルドを開始",
			"formReinputBtnTitle": "フォームを再入力してビルド",
			"formRePackBtnTitle": "既存のデータを使用して直接ビルド",
			"formDownloadApkBtnTitle": "APKファイルをダウンロード",
			"formDownloadPublishBtnTitle": "ローカルに公開したファイルをダウンロード",
			"refreshStatusBtnTitle": "ステータスを更新",
			"formUploadLogoBtnTitle": "画像をアップロード",

            "messageO2cloudNotEnable": "O2クラウドが有効化されていないか接続できません！",
			"messageO2cloudNotLogin": "まずO2クラウドにログインしてください！",
			"messageO2cloudLoginFail": "Appパッケージングサーバーのログインに失敗しました！",
			"statusOrderInline": "待機中...",
			"statusPacking": "パッケージング中...",
			"statusPackEnd": "パッケージング完了",
			"statusPackError": "パッケージングエラー",
			"publishStatusNone": "未公開",
			"publishStatusDoing": "公開中...",
			"publishStatusCompleted": "公開が完了し、アプリをインストールするには画面に表示されたQRコードをスキャンしてください！",
			"publishStatusFail": "公開に失敗しました。再試行するか、管理者に連絡してください！",
			"messageSubmitNotAtStatus": "現在、パッケージング中です。後でもう一度試してください！",
			"messageAppnameNotEmpty": "App名は空白にできません！",
			"messageAppnameLenMax6": "App名は6文字を超えることはできません！",
			"messageAppLogoNotEmpty": "ロゴ画像を再アップロードしてください！",
			"messageAppLogoNeedPng": "ロゴ画像はPNG形式である必要があります！",
			"messagePortocolNotEmpty": "HTTPプロトコルは空白にできません！",
			"messageHostNotEmpty": "中央サーバードメインは空白にできません！",
			"messageHostFormatError": "中央サーバードメインまたはIPアドレスを入力してください。例: www.o2oa.net。 'http'などの接頭辞は含めないでください！",
			"messagePortNotEmpty": "中央サーバーポート番号は空白にできません！",
			"messageContext_not_empty": "中央サーバーコンテキストは空白にできません！",
			"messagePortocolMustBeHttpHttps": "HTTPプロトコルは 'http' または 'https' のみが許可されています！",
			"messageAlertTitle": "提出確認",
			"messageAlertSubmit": "本当に提出しますか？現在のフォーム情報はモバイルアプリとしてパッケージ化されますか？",

            "statusLabel": "現在の状態",
			"publishStatusLabel": "公開状態",
			"formAppName": "App名",
			"formAppNameTip": "Appデスクトップに表示される名前、6文字を超えないようにしてください",
			"formLogo": "ロゴ画像",
			"formLogoTip": "Appデスクトップに表示されるロゴ画像、必ずPNG形式である必要があります",
			"formProtocol": "HTTPプロトコル",
			"formProtocolTip": "http / https",
			"formHost": "ドメイン",
			"formHostTip": "中央サーバードメインまたはIPアドレス、例: www.o2oa.net",
			"formPort": "ポート番号",
			"formPortTip": "中央サーバーポート番号、例: 20030",
			"formContext": "コンテキスト",
			"formContextTip": "中央サーバーコンテキスト、例: /x_program_center",
			"formUrlMapping": "プロキシURLマッピング",
			"formUrlMappingTip": "サーバー外部でプロキシアドレスを使用する場合に使用、例: { \"demo.o2oa.net:20020\": \"demo.o2oa.net/dev/app\" }",
			"formAppVersionName": "Appバージョン名",
			"formAppVersionNameTip": "Appのバージョン名、例: v1.0.0。このフィールドはデフォルトで記入する必要はありません！",
			"formAppBuildNo": "Appバージョン番号",
			"formAppBuildNoTip": "Appのバージョン番号、正の整数である必要があります、例: 100。このフィールドはデフォルトで記入する必要はありません！",
			"formEnableOuterPackage": "外部パッケージ名を有効にする",
			"formEnableOuterPackageTip": "外部パッケージ名を有効にすると、公式にリリースされたAPPとの競合や上書きを防ぐことができます"
        },

        "mpMenu": {
            "mpweixinInfo": "⚠️ WeChat公式アカウントメニュー機能を使用するには、まず関連する構成ファイル[mpweixin.json]を有効にし、WeChat公式アカウントの管理バックエンドで開発モジュールからサーバー設定を有効にする必要があります！",
			"mpweixin": "公式アカウント",
			"publishMpweixin": "WeChat公式アカウントに公開",
			"publishToWxmp": "注意！現在の操作はすべての保存されたメニューデータをWeChat公式アカウントに上書きできます。続行しますか？",
			"publishSuccess": "公開に成功し、24時間後にモバイルで同期されます！",
			"subscribeMpweixin": "フォロー応答",
			"subscribeMpweixin_desc": "WeChat公式アカウントに新しいユーザーがフォローすると自動的に送信されるメッセージの内容",
			"subscribeContentErrorEmpty": "応答メッセージの内容を空にすることはできません！",
			"subscribeMpweixin_save": "保存",
			"deleteMenuBtnTitle": "メニューを削除",
			"defaultNewName": "新しいメニュー",
			"formNameLabel": "メニュー名",
			"formOrderLabel": "メニューの並べ替え番号",
			"formRadioLabel": "メニューの内容",
			"formRadioTypeMsg": "メッセージを送信",
			"formRadioTypeUrl": "ウェブページに移動",
			"formRadioTypeMiniprogram": "小プログラムに移動",

            "formTypeMsgTips": "このメニューをクリックすると、以下のテキストをユーザーに送信します。未認証のサブスクリプションアカウントはテキストメッセージをサポートしていません。",
			"formTypeMsgLabel": "テキストメッセージ",
			"formTypeMsgErrorEmpty": "テキストメッセージの内容を空にすることはできません！",
			"formSubscribeContentErrorEmpty": "応答メッセージの内容を空にすることはできません！",
			"formTypeUrlTips": "このメニューをクリックすると、以下のリンクに移動します。",
			"formTypeUrlLabel": "ページのアドレス",
			"formTypeUrlErrorEmpty": "ページのアドレスを空にすることはできません！",
			"formTypeMiniprogramTips": "このメニューをクリックすると、以下の小プログラムに移動します。",
			"formTypeMiniprogramAppidLabel": "小プログラムID",
			"formTypeMiniprogramAppidPlaceholder": "小プログラムID、WeChatの小プログラム管理バックエンドで確認してください。",
			"formTypeMiniprogramAppidErrorEmpty": "小プログラムIDを空にすることはできません！",
			"formTypeMiniprogramPathLabel": "小プログラムのパス",
			"formTypeMiniprogramPathPlaceholder": "小プログラムのパス、WeChatの小プログラム管理バックエンドで確認してください。",
			"formTypeMiniprogramPathErrorEmpty": "小プログラムのパスを空にすることはできません！",
			"formTypeMiniprogramUrlLabel": "代替ウェブページ",
			"formTypeMiniprogramUrlPlaceholder": "代替ウェブページ、旧バージョンのWeChatではこの代替ウェブページが開きます。",
			"formTypeMiniprogramUrlErrorEmpty": "代替ウェブページを空にすることはできません！",
			"formNameTips4": "中国語、英語、数字のみサポートされ、4文字を超えないようにしてください。",
			"formNameTips6": "中国語、英語、数字のみサポートされ、6文字を超えないようにしてください。",
			"formOrderTips": "数字のみサポートされ、6文字を超えないようにしてください。ソートは文字列で行われます。",
			"msgFirstMaxLen": "1次メニューは最大3つしか作成できません！",
			"menuMsgSubMaxLen": "2次メニューは最大5つしか作成できません！",
			"menuMsgParentNotSave": "上位メニューデータが保存されていません。まずデータを保存してください！",
			"menuDeleteAlertMsg": "このデータを削除しますか？サブメニューも同時に削除されます。",
			"menuDeleteSuccess": "データの削除に成功しました！",
			"menuSaveSuccess": "データの保存に成功しました！",
			"formNameErrorEmpty": "メニュー名を空にすることはできません！",
			"formNameErrorMaxLen4": "メニュー名の文字数は4文字を超えることはできません！",
			"formNameErrorMaxLen6": "メニュー名の文字数は6文字を超えることはできません！",
			"formNameError": "上限を超えた文字数",
			"formOrderErrorEmpty": "メニューのソート番号を空にすることはできません！",
			"formOrderErrorNotNumber": "メニューのソート番号には数字しか入力できません！",
			"formOrderErrorMaxLen": "メニューのソート番号の文字数は6文字を超えることはできません！"
        }
    },
    "_pushConfig": {
        "pushType": "メッセージプッシュサービス",
		"pushTypeInfo": "O2OAはJPushおよびHuawei Pushサービスをサポートしており、必要に応じてプッシュサービスを選択できます。",
		"pushTypeData": [
			{"value": "jpush", "label": "jpush", "text": "JPushメッセージプッシュサービス"},
			{"value": "none", "label": "none", "text": "メッセージプッシュを無効にする"}
		],

		"appKey": "JPushアプリキー",
		"masterSecret": "JPushマスターシークレット",
		"appKeyInfo": "JPushアプリのアプリキー",
		"masterSecretInfo": "JPushアプリのマスターシークレット",

		"appId": "Huawei PushアプリID",
		"appSecret": "Huawei Pushアプリシークレット",
		"appIdInfo": "Huawei PushアプリのアプリID",
		"appSecretInfo": "Huawei Pushアプリのアプリシークレット"
    },
    "_messageConfig": {
        "messageConsumers": "チャネル設定",
		"messageType": "タイプ設定",
		"messageLoader": "ローダー",
		"messageFilter": "フィルター",

		"consumerTypes": {
			"ws": "WebSocket",
			"pmsinner": "プッシュメッセージ",
			"calendar": "カレンダー",
			"dingding": "DingDing",
			"welink": "WeLink",
			"qiyeweixin": "企業微信",
			"mpweixin": "WeChat公式アカウント",
			"kafka": "Kafka",
			"activemq": "ActiveMQ",
			"restful": "Restful",
			"mail": "メール",
			"jdbc": "JDBC",
			"table": "データテーブル",
			"hadoop": "Hadoop",
			"andfx": "モバイルオフィスメッセージ"
		},
        "consumerInfoTitle": "メッセージチャネル設定",
		"consumerInfo": "O2OAシステムではさまざまなメッセージチャネルを提供しており、ここでどの方法でメッセージを送信するかを設定できます。",
		"consumerInfo2": "メッセージの設定に関する詳細情報は、次をご覧ください：<a href='https://www.o2oa.net/search.html?q=%E6%B6%88%E6%81%AF%E9%85%8D%E7%BD%AE' target='_blank'>メッセージ</a>",

		"addConsumer": "メッセージチャネルの追加",
		"consumerLabel": {
			"key": "チャネル名",
			"type": "タイプ",
			"filter": "フィルター",
			"loader": "ローダー",
			"startTlsEnable": "トランスポートレベルセキュリティ（TLS）の有効化"
		},
		"none": "なし",
		"editConsumer": "メッセージチャネルの編集",

		"inputKey": "メッセージチャネル名を入力してください",
		"hasKey": "メッセージチャネル名がすでに存在します。他の名前を使用してください。",

        "consumerData": {
            "kafka": ['bootstrapServers', 'topic', 'securityProtocol', 'saslMechanism', 'saslMechanism', 'username', 'password'],
            "activemq": ['url', 'queueName', 'username', 'password'],
            "restful": ['url', 'method', 'internal'],
            "mail": ['host', 'port', 'sslEnable', 'auth', 'startTlsEnable', 'from', 'password'],
            "jdbc": ['driverClass', 'url', 'catalog', 'schema', 'table', 'username', 'password'],
            "table": ['table'],
            "hadoop": ['fsDefaultFS', 'path', 'username']
        },

        "messageTypeTitle": "メッセージタイプ設定",
		"messageTypeInfo": "O2OAシステムに組み込まれたさまざまなイベントはメッセージを送信できます。ここでは、これらのイベントがどのチャネルを介してメッセージを送信する必要があるかを設定できます。カスタムメッセージタイプを追加することもできます。",

		"noConsumer": "このタイプのメッセージに対する送信チャネルが選択されていません。",
		"selectConsumer": "チャネルを選択",
		"addTmpConsumer": "チャネルを追加",

		"addMessageType": "メッセージタイプを追加",
		"newMessageData": {
			"key": "メッセージ識別子",
			"description": "説明"
		},
		"inputMessageKey": "メッセージ識別子を入力してください",
		"hasMessageKey": "メッセージ識別子は既に存在します。別の識別子を使用してください。",

		"deleteTypeTitle": "メッセージタイプの削除確認",
		"deleteTypeInfo": "メッセージタイプ“{name}”を削除しますか？",

        "filterConfigTitle": "メッセージフィルター設定",
		"filterConfigInfo": "メッセージチャネルでフィルターを使用できます。フィルターはサーバーサイドスクリプトで、メッセージを送信する前に呼び出されます。フィルターがtrueを返すとメッセージが送信され、falseを返すとメッセージは送信されません。",
		"addFilter": "メッセージフィルターを追加",
		"filterKey": "フィルター名",
		"inputFilterKey": "フィルター名を入力してください",
		"hasFilterKey": "フィルター名は既に存在します。別の名前を使用してください。",
		"deleteFilterTitle": "フィルターの削除確認",
		"deleteFilterInfo": "フィルター“{name}”を削除しますか？",

		"loaderConfigTitle": "メッセージローダー設定",
		"loaderConfigInfo": "メッセージチャネルでローダーを使用できます。ローダーはサーバーサイドスクリプトで、メッセージの内容を送信前に変更するために使用されます。メッセージを送信する前にJSON形式のデータを返す必要があります。このデータは送信するメッセージの内容として使用されます。",
		"addLoader": "メッセージローダーを追加",
		"loaderKey": "ローダー名",

        "inputLoaderKey": "ローダー名を入力してください",
		"hasLoaderKey": "ローダー名は既に存在します。別の名前を使用してください。",

		"deleteLoaderTitle": "ローダーの削除確認",
		"deleteLoaderInfo": "ローダー“{name}”を削除しますか？",

		"deleteConsumerTitle": "メッセージチャネルの削除確認",
		"deleteConsumerInfo": "メッセージチャネル“{name}”を削除しますか？",

		"loaderComment": "/*\nmessage オブジェクトはメッセージ本体で、スクリプトの実行コンテキストに自動的に注入されます。ここには4つのフィールドがあります。\nmessage.title: タイトル\nmessage.person: 送信対象\nmessage.type: メッセージの種類、例: task_create\nmessage.body: メッセージ本体、例: task_createの種類のメッセージにはJSON形式のtask(タスク)データが格納されています。\nreturn 戻されたmessageオブジェクト\n*/\nreturn message;",
		"filterComment": "/*\nmessage オブジェクトはメッセージ本体で、スクリプトの実行コンテキストに自動的に注入されます。ここには4つのフィールドがあります。\nmessage.title: タイトル\nmessage.person: 送信対象\nmessage.type: メッセージの種類、例: task_create\nmessage.body: メッセージ本体、例: task_createの種類のメッセージにはJSON形式のtask(タスク)データが格納されています。\nreturn 戻されたboolean、trueは送信が必要、falseは送信しないことを示します。\n*/\nreturn true;"
    }
}
