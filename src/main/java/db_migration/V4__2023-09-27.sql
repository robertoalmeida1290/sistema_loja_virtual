CREATE TABLE public.tabela_acesso_and_point(
  nome_end_point character varying,
  qtd_acesso_end_point integer);

	jdbcTemplate.execute("begin; update tabela_acesso_end_potin set qtd_acesso_end_point = qtd_acesso_end_point + 1 where nome_end_point = 'END-POINT-NOME-PESSOA-FISICA'; commit;");
