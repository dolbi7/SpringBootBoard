
//로그아웃 처리
const logout = () => {
	if(!confirm("로그아웃 하시겠습니까?")) return;
	
	let authKey = getCookie('authKey');
	
	if(authKey !== undefined) 		
			document.cookie = 'authKey=' + authKey + ';path=/; max-age=0';				

	document.location.href='/member/logout';		
}

const getCookie = (name) => {
	let matches = document.cookie.match(new RegExp(
	  "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
	));
	  return matches ? decodeURIComponent(matches[1]) : undefined;
}